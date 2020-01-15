package code;


import code.lib.ImageUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Ticket {

    private String index, school, fio;
    private Image QRCode, stamp, icon, resultImage;


    public Ticket(String index, String school, String fio, String QRCode, Image icon) {
        this.index = index;
        this.school = school;
        this.fio = fio;
        //QRCODE
        this.icon = icon;
        resultImage = new Image("/img/template.png");
        stamp = new Image("/img/stamp_r.png");
        writeStamp();
        makeResultImage();
    }
   /* private Image QRGenerate(){
        Image qr = new Image(null);

    }
*/

    public Image getResultImage() {
        return resultImage;
    }

    public Image getResultScaledImage() {
        return ImageUtils.resizeImage(resultImage,295, 178);
    }

    public String getIndex(){
        return index;
    }

    private void writeStamp() {
        String[] fioArr = fio.split(" ");
        BufferedImage stampAwp = SwingFXUtils.fromFXImage(stamp, null);
        Graphics2D gr = stampAwp.createGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.setColor(new Color(0, 85, 166));

        gr.setFont(new Font("Arial", Font.BOLD, 25));
        printCenterString(gr, fioArr[0].toUpperCase(), 150, 50, 110);
        for (int i = 1; i < fioArr.length; ++i) {
            printCenterString(gr, fioArr[i], 150, 50, 110 + i * 25);
        }

        //TODO
        //gr.rotate(Math.toRadians(25));
        //gr.drawRenderedImage(stampAwp, null);
        //gr.drawImage(stampAwp,0,0, null);
        //gr.dispose();

        stamp = SwingFXUtils.toFXImage(stampAwp, null);
    }



    private void makeResultImage() {
        BufferedImage resultAwp = SwingFXUtils.fromFXImage(resultImage, null);
        Graphics2D gr = (Graphics2D) resultAwp.getGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        gr.drawImage(SwingFXUtils.fromFXImage(stamp, null), 1250, 45, null);
        //gr.drawImage(SwingFXUtils.fromFXImage(resizeImage(icon, 300,300), null), 150, 600, null);

        if(icon!=null) gr.drawImage(SwingFXUtils.fromFXImage(ImageUtils.fitByWidth(icon, 200), null), 60, 675, null);

        gr.setFont(new Font("Arial", Font.ITALIC, 30));
        gr.setColor(new Color(198, 198, 198));
        gr.drawString("Номер Талона: " + index, 330, 900);
        gr.drawString(school, 80, 80);
        resultImage = SwingFXUtils.toFXImage(resultAwp, null);
    }

    private void printCenterString(Graphics gr, String str, int width, int X, int Y) {
        int stringLen = (int)
                gr.getFontMetrics().getStringBounds(str, gr).getWidth();
        int start = width / 2 - stringLen / 2;
        gr.drawString(str, start + X, Y);
    }
}