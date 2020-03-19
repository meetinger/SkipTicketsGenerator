package ru.yanchikdev;


import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import ru.yanchikdev.lib.ImageUtils;
import ru.yanchikdev.lib.MathUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


public class Ticket implements Comparable<Ticket> {

    private String school, fio, QRString, quote;
    private BufferedImage QRCodeIMG, stamp, icon, resultImage;
    int index;
    int iconX, iconY, iconSize, bgthreshold;

    public Ticket(int index, String school, String fio, String QRCode, String quote, Image icon, int iconX, int iconY,int iconSize, int bgthreshold) {
        this.index = index;
        this.school = school;
        this.fio = fio;
        this.quote = quote;
        this.QRString = QRCode.replace("%number%", String.valueOf(this.index)).replace("%fio%", this.fio).replace("%school%", this.school);
        this.icon = SwingFXUtils.fromFXImage(icon, null);
        this.iconX = iconX;
        this.iconY = iconY;
        this.iconSize = iconSize;
        this.bgthreshold = bgthreshold;

        resultImage = SwingFXUtils.fromFXImage(new Image(String.valueOf(getClass().getClassLoader().getResource("img/template.png"))), null);
        stamp = SwingFXUtils.fromFXImage(new Image(String.valueOf(getClass().getClassLoader().getResource("img/stamp_r.png"))), null);
        writeQRCode();
        writeStamp();
        makeResultImage();
    }

    public BufferedImage getResultImage() {
        return resultImage;
    }

    public BufferedImage getResultScaledImage() {
        return ImageUtils.resizeImage(resultImage, 295, 178);
    }

    public int getIndex() {
        return index;
    }

    private void writeQRCode() {

        File qrfile = QRCode.from(QRString).to(ImageType.PNG).withSize(350, 350).withCharset("UTF-8").file();
        try {
            QRCodeIMG = ImageIO.read(qrfile);
        } catch (Exception e) {

        }
    }

    private void writeStamp() {
        String[] fioArr = fio.split(" ");
        BufferedImage stampAwp = stamp;
        Graphics2D gr = stampAwp.createGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gr.setColor(new Color(0, 85, 166));

        int fontSize = 25;

        gr.setFont(new Font("Arial", Font.BOLD, fontSize));
        if (fioArr.length != 0) {

            int maxStrLen = (int) gr.getFontMetrics().getStringBounds(fioArr[0], gr).getWidth();
            int maxIndex = 0;

            for (int i = 1; i < fioArr.length; ++i) {
                if (maxStrLen < (int) gr.getFontMetrics().getStringBounds(fioArr[i], gr).getWidth()) {
                    maxStrLen = (int)
                            gr.getFontMetrics().getStringBounds(fioArr[i], gr).getWidth();
                    maxIndex = i;
                }
            }

            while (maxStrLen >= 155) {
                gr.setFont(new Font("Arial", Font.BOLD, --fontSize));
                maxStrLen = (int) gr.getFontMetrics().getStringBounds(fioArr[maxIndex], gr).getWidth();
            }

            for (int i = 0; i < fioArr.length; ++i) {
                if (i == 0) {
                    ImageUtils.printCenterString(gr, fioArr[i].toUpperCase(), 150, 50, 110);
                } else {
                    ImageUtils.printCenterString(gr, fioArr[i], 150, 50, 110 + i * fontSize);
                }
            }
        }
        stamp = ImageUtils.rotateImageByDegrees(stampAwp, -35);
    }

    private void removeBG() {
        for (int y = 0; y < icon.getHeight(); ++y) {
            for (int x = 0; x < icon.getWidth(); ++x) {
                int argb = icon.getRGB(x, y);
                int a = (argb >> 24) & 0xff;
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = (argb) & 0xff;
                if (r >= bgthreshold && g >= bgthreshold && b >= bgthreshold) {
                    icon.setRGB(x, y, new Color(r, g, b, 0).getRGB());
                }
            }
        }

    }

    private void makeResultImage() {
        BufferedImage resultAwp = resultImage;
        Graphics2D gr = (Graphics2D) resultAwp.getGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gr.drawImage(stamp, 1210, 10, null);

        gr.drawImage(QRCodeIMG, 1210, 620, null);

        icon = ImageUtils.fitByWidth(icon, iconSize);

        if (bgthreshold != -1) {
            removeBG();
        }
        gr.drawImage(icon, 60 + iconX, 675 + iconY, null);


        int fontSize = 30;

        gr.setFont(new Font("Arial", Font.ITALIC, fontSize));
        gr.setColor(new Color(198, 198, 198));
        gr.drawString("Номер Талона: " + index, 330, 900);

        ImageUtils.printCenterString(gr, school, 475, 80, 80);

        String[] quoteArr = quote.replace("\\n", "\n").split("\\r?\\n");
        gr.setColor(Color.black);
        gr.setFont(new Font("Verdana", Font.PLAIN, 30));
        for (int i = 0; i < quoteArr.length; ++i) {
            ImageUtils.printCenterAdaptiveString(gr, quoteArr[i], 150, 275, 650 + i * 30);
        }

        resultImage = resultAwp;
    }

    @Override
    public int compareTo(Ticket t) {
        return this.getIndex() - t.getIndex();
    }

}