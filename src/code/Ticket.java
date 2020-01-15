package code;


import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Ticket {

    private String index, school, fio;
    private Image QRCode, stamp, teacher, resultImage;


    public Ticket(String index, String school, String fio, String QRCode, Image teacher) {
        this.index = index;
        this.school = school;
        this.fio = fio;
        //QRCODE
        this.teacher = teacher;
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

    //TODO
    public Image getResultScaledImage() {
        BufferedImage resultAwp = SwingFXUtils.fromFXImage(resultImage, null);
        BufferedImage scaled = new BufferedImage(295, 159, Transparency.TRANSLUCENT);
        Graphics2D gr = resultAwp.createGraphics();
        Graphics2D sc = scaled.createGraphics();

        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.scale(0.5, 0.5);
        gr.drawImage(resultAwp, 0, 0, null);
        gr.dispose();
        //gr.drawImage(resultAwp, );
        //gr.drawImage(scaled,0,0,null);
        sc.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        sc.drawImage(resultAwp, 0, 0, null);

        System.out.println(resultAwp.getWidth());
        return SwingFXUtils.toFXImage(resultAwp, null);
    }
   /* private void writeStamp() {
        String[] fioArr = fio.split(" ");
        BufferedImage stampAwp = SwingFXUtils.fromFXImage(stamp, null);
        Graphics2D gr = (Graphics2D) stampAwp.getGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.setColor(new Color(0, 85, 166));

        gr.setFont(new Font("Arial", Font.BOLD, 50));
        printCenterString(gr, fioArr[0].toUpperCase(), 300, 100, 200);
        printCenterString(gr, fioArr[1], 300, 100, 250);
        printCenterString(gr, fioArr[2], 300, 100, 300);

        gr.drawImage(stampAwp.getScaledInstance(250, 250, 0), 0, 0, null);
        BufferedImage tmp = stampAwp;

        //stampAwp = stampAwp.getScaledInstance(250, 250, 0).getBufferedImage();

        //gr.drawImage(stampAwp, 0, 250, 250, 0, null, null, stampAwp.getWidth(), stampAwp.getHeight());
        stamp = SwingFXUtils.toFXImage(stampAwp, null);
    }*/

    private void writeStamp() {
        String[] fioArr = fio.split(" ");
        BufferedImage stampAwp = SwingFXUtils.fromFXImage(stamp, null);
        Graphics2D gr = stampAwp.createGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.setColor(new Color(0, 85, 166));

        gr.setFont(new Font("Arial", Font.BOLD, 25));
        printCenterString(gr, fioArr[0].toUpperCase(), 150, 50, 100);
        for (int i = 1; i < fioArr.length; ++i) {
            printCenterString(gr, fioArr[1], 150, 50, 100 + i * 25);
        }

        //TODO
        //gr.rotate(Math.toRadians(25));
        //gr.drawRenderedImage(stampAwp, null);
        //gr.drawImage(stampAwp,0,0, null);
        //gr.dispose();

        stamp = SwingFXUtils.toFXImage(stampAwp, null);
    }

    private Image resizeImage(Image toScale, int width, int height) {
        BufferedImage toScaleAwp = SwingFXUtils.fromFXImage(toScale, null);
        if (width < 1) {
            width = 1;
        }
        if (height <= 0) {
            double aspectRatio = (double) width / toScale.getWidth() * 0.5;
            height = (int) Math.ceil(toScale.getHeight() * aspectRatio);
        }
        BufferedImage resized = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        BufferedImage scaled = (BufferedImage) toScaleAwp.getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT);
        Graphics2D gr = resized.createGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.drawImage(scaled, 0, 0, null);
        //resized.getGraphics().drawImage(scaled, 0, 0, null);
        return SwingFXUtils.toFXImage(scaled, null);
    }

    private void makeResultImage() {
        BufferedImage resultAwp = SwingFXUtils.fromFXImage(resultImage, null);
        Graphics2D gr = (Graphics2D) resultAwp.getGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.drawImage(SwingFXUtils.fromFXImage(stamp, null), 1425, 45, null);
        gr.drawImage(SwingFXUtils.fromFXImage(resizeImage(teacher, 120,50), null), 150, 600, null);
        resultImage = SwingFXUtils.toFXImage(resultAwp, null);
    }

    private void printCenterString(Graphics gr, String str, int width, int X, int Y) {
        int stringLen = (int)
                gr.getFontMetrics().getStringBounds(str, gr).getWidth();
        int start = width / 2 - stringLen / 2;
        gr.drawString(str, start + X, Y);
    }
}