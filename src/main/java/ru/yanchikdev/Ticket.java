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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collections;


public class Ticket implements Comparable<Ticket>{

    private String index, school, fio, QRString, quote;
    private BufferedImage QRCodeIMG, stamp, icon, resultImage;

    public Ticket(String index, String school, String fio, String QRCode, String quote ,Image icon) {
        this.index = index;
        this.school = school;
        this.fio = fio;
        this.quote = quote;
        /*if(QRCode.equals("%number%")) {
            this.QRString = this.index;
        }else if(QRCode.equals("%fio%")){
            this.QRString = this.fio;
        }else if(QRCode.equals("%fio%+%number%")){
            this.QRString = this.fio+": " + this.index;
        }else{
            this.QRString =
        }*/
        this.QRString = QRCode.replace("%number%", this.index).replace("%fio%", this.fio).replace("%school%", this.school);


        //QRCODE
        this.icon = SwingFXUtils.fromFXImage(icon, null);
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

    public String getIndex() {
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
        //stamp = ImageUtils.rotateImageByDegrees(stampAwp, MathUtils.RandomIntInInterval(-45, -25, 25, 45));
        stamp = ImageUtils.rotateImageByDegrees(stampAwp, -35);



    }


    private void makeResultImage() {
        BufferedImage resultAwp = resultImage;
        Graphics2D gr = (Graphics2D) resultAwp.getGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gr.drawImage(stamp, 1210, 10, null);

        gr.drawImage(QRCodeIMG, 1210, 620, null);

        if (icon != null) {
            gr.drawImage(ImageUtils.fitByWidth(icon, 200), 60, 675, null);
        }

        int fontSize = 30;

        gr.setFont(new Font("Arial", Font.ITALIC, fontSize));
        gr.setColor(new Color(198, 198, 198));
        gr.drawString("Номер Талона: " + index, 330, 900);

        ImageUtils.printCenterString(gr, school, 475, 80, 80);

        String[] quoteArr = quote.replace("\\n","\n").split("\\r?\\n");
        gr.setColor(Color.black);
        gr.setFont(new Font("Verdana", Font.PLAIN, 30));
        for(int i = 0; i < quoteArr.length; ++i){
            ImageUtils.printCenterAdaptiveString(gr, quoteArr[i], 150, 275, 650+i*30);
        }

        resultImage = resultAwp;
    }

    @Override
    public int compareTo(Ticket t){
        return Integer.parseInt(this.getIndex()) - Integer.parseInt(t.getIndex());
    }

}