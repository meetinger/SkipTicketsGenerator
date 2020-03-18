package ru.yanchikdev;

import ru.yanchikdev.lib.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TicketStack {
    ArrayList<Ticket> tickets = new ArrayList<Ticket>();
    BufferedImage toPrint;
    int devider = 4;
    int imgH;
    int imgW;

    public TicketStack(ArrayList<Ticket> tickets) {
        imgH = tickets.get(0).getResultImage().getHeight() * devider;
        imgW = tickets.get(0).getResultImage().getWidth() * (devider-1);
        toPrint = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
        this.tickets = tickets;
        this.writeTickets();
    }

    public void writeTickets() {
        Graphics2D gr = toPrint.createGraphics();
        int height = imgH / devider;
        int width = imgW / (devider-1);
        for (int i = 0; i < tickets.size(); ++i) {
            gr.drawImage(ImageUtils.fitByHeight(tickets.get(i).getResultImage(), height), width * (i / devider), height * (i % devider), null);
        }
    }

    public BufferedImage getResultImage() {
        return toPrint;
    }

    public String getIndexes() {
        return Math.min(tickets.get(0).getIndex(), tickets.get(tickets.size() - 1).getIndex()) + "-" + Math.max(tickets.get(0).getIndex(), tickets.get(tickets.size() - 1).getIndex());
    }
}
