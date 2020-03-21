package ru.yanchikdev;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TicketsWorker extends Thread {
    String school, fio, qrcode, quote, path;
    int leftborder, rightborder, progress, xAdd, yAdd, iconSize, BGThreshold;

    BufferedImage icon;


    TicketsWorker(String school, String fio, String qrcode, String path, String quote, BufferedImage icon, int xAdd, int yAdd,int iconSize, int BGThreshold,
           int leftborder, int rightborder) {
        this.school = school;
        this.fio = fio;
        this.qrcode = qrcode;
        this.path = path;
        this.leftborder = leftborder;
        this.rightborder = rightborder;
        this.icon = icon;
        this.quote = quote;
        this.xAdd = xAdd;
        this.yAdd = yAdd;
        this.iconSize = iconSize;
        this.BGThreshold = BGThreshold;
    }


    @Override
    public void run() {
        for (int i = leftborder; i <= rightborder; ++i) {
            Ticket tmp = new Ticket(i, this.school, this.fio, this.qrcode, this.quote, this.icon, this.xAdd, this.yAdd, this.iconSize, this.BGThreshold);
            this.writeTicketToFile(tmp);
            Main.control.addTicketToArray(tmp);
            this.progress = i - leftborder + 1;
        }
        Main.control.threadsEnd();
        Main.control.startStacksGen();
    }

    private void writeTicketToFile(Ticket ticket) {
        try {
            ImageIO.write(ticket.getResultImage(), "png", new File(path + "/tickets/ticket" + ticket.getIndex() + ".png"));
        } catch (IOException e) {
            FxDialogs.showError("Ошибка", e.getMessage());
        }
    }

    public int getProgress(){
        return this.progress;
    }

}
