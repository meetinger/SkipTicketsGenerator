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

        //tampAwp = stampAwp.getScaledInstance(250, 250, 0).getBufferedImage();

        //gr.drawImage(stampAwp, 0, 250, 250, 0, null, null, stampAwp.getWidth(), stampAwp.getHeight());
        stamp = SwingFXUtils.toFXImage(stampAwp, null);
    }*/

    private void writeStamp() {
        String[] fioArr = fio.split(" ");
        BufferedImage stampAwp = SwingFXUtils.fromFXImage(stamp, null);
        Graphics2D gr = (Graphics2D) stampAwp.getGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.setColor(new Color(0, 85, 166));

        gr.setFont(new Font("Arial", Font.BOLD, 25));
        printCenterString(gr, fioArr[0].toUpperCase(), 150, 50, 100);
        for(int i = 1; i<fioArr.length;++i){
            printCenterString(gr, fioArr[1], 150, 50, 100+i*25);
        }

        stamp = SwingFXUtils.toFXImage(stampAwp, null);
    }


    private void makeResultImage() {
        BufferedImage resultAwp = SwingFXUtils.fromFXImage(resultImage, null);
        Graphics2D gr = (Graphics2D) resultAwp.getGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gr.drawImage(SwingFXUtils.fromFXImage(stamp, null), 1425, 45, null);
        resultImage = SwingFXUtils.toFXImage(resultAwp, null);
    }

    private void printCenterString(Graphics gr, String str, int width, int X, int Y) {
        int stringLen = (int)
                gr.getFontMetrics().getStringBounds(str, gr).getWidth();
        int start = width / 2 - stringLen / 2;
        gr.drawString(str, start + X, Y);
    }
}