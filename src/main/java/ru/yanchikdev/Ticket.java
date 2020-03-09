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


public class Ticket {

    private String index, school, fio, QRString;
    private BufferedImage QRCodeIMG, stamp, icon, resultImage;

    public Ticket(String index, String school, String fio, String QRCode, Image icon) {
        this.index = index;
        this.school = school;
        this.fio = fio;
        /*if(QRCode.equals("%number%")) {
            this.QRString = this.index;
        }else if(QRCode.equals("%fio%")){
            this.QRString = this.fio;
        }else if(QRCode.equals("%fio%+%number%")){
            this.QRString = this.fio+": " + this.index;
        }else{
            this.QRString =
        }*/
        this.QRString = QRCode.replace("%number%", this.index).replace("%fio%", this.fio);


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

       File qrfile =  QRCode.from(QRString).to(ImageType.PNG).withSize(350, 350).withCharset("UTF-8").file();
        try {
            QRCodeIMG = ImageIO.read(qrfile);
        }catch (Exception e){

        }
    }

    private void writeStamp() {
        String[] fioArr = fio.split(" ");
        BufferedImage stampAwp = stamp;
        Graphics2D gr = stampAwp.createGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.setColor(new Color(0, 85, 166));

        gr.setFont(new Font("Arial", Font.BOLD, 25));
        printCenterString(gr, fioArr[0].toUpperCase(), 150, 50, 110);

        for (int i = 1; i < fioArr.length; ++i) {
            printCenterString(gr, fioArr[i], 150, 50, 110 + i * 25);
        }
        stamp = ImageUtils.rotateImageByDegrees(stampAwp, MathUtils.RandomIntInInterval(-45, -25, 25, 45));
    }


    private void makeResultImage() {
        BufferedImage resultAwp = resultImage;
        Graphics2D gr = (Graphics2D) resultAwp.getGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        gr.drawImage(stamp, 1210, 10, null);

        gr.drawImage(QRCodeIMG, 1210, 630, null);

        if (icon != null) gr.drawImage(ImageUtils.fitByWidth(icon, 200), 60, 675, null);

        gr.setFont(new Font("Arial", Font.ITALIC, 30));
        gr.setColor(new Color(198, 198, 198));
        gr.drawString("Номер Талона: " + index, 330, 900);
        gr.drawString(school, 80, 80);
        resultImage = resultAwp;
    }

    private void printCenterString(Graphics gr, String str, int width, int X, int Y) {
        int stringLen = (int)
                gr.getFontMetrics().getStringBounds(str, gr).getWidth();
        int start = width / 2 - stringLen / 2;
        gr.drawString(str, start + X, Y);
    }
}