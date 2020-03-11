package ru.yanchikdev;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Worker extends Thread {
    String school, fio, qrcode, path;
    int leftborder, rightborder, progress;
    Image icon;


    Worker(String school, String fio, String qrcode, String path, Image icon,
           int leftborder, int rightborder) {
        this.school = school;
        this.fio = fio;
        this.qrcode = qrcode;
        this.path = path;
        this.leftborder = leftborder;
        this.rightborder = rightborder;
        this.icon = icon;
    }


    @Override
    public void run() {
        for (int i = leftborder; i <= rightborder; ++i) {
            this.writeTicketToFile(new Ticket(String.valueOf(i), this.school, this.fio, this.qrcode, this.icon));
            this.progress = i - leftborder + 1;
        }
    }

    private void writeTicketToFile(Ticket ticket) {
        try {
            ImageIO.write(ticket.getResultImage(), "png", new File(path + "/ticket" + ticket.getIndex() + ".png"));
        } catch (IOException e) {
            FxDialogs.showError("Ошибка", e.getMessage());
        }
    }

    public int getProgress(){
        return this.progress;
    }
}
