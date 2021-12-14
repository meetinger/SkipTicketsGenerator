package ru.yanchikdev;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StackWorker extends Thread {

    ArrayList<Ticket> stack = new ArrayList<>();
    String path;
    double progress;

    public StackWorker(ArrayList<Ticket> stack, String path) {
        this.stack = stack;
        this.path = path;
    }

    @Override
    public void run() {

        if (stack.size() <= 12) {
            TicketStack tmp = new TicketStack(stack);
            writeStackToFile(tmp);
        }
        for (int i = 0; i <= stack.size() / 12; ++i) {
            //System.out.println("Размер стака: "+stack.size());
            int lastindex = Math.min((i + 1) * 12, stack.size());
            if (i * 12 + 1 != lastindex) {
                TicketStack tmp = new TicketStack(new ArrayList<Ticket>(stack.subList(i * 12, lastindex)));
                writeStackToFile(tmp);
            }
            progress = (double) (i + 1) / (stack.size() / 12);
        }
    }


    private void writeStackToFile(TicketStack stack) {
        try {
            ImageIO.write(stack.getResultImage(), "png", new File(path + "/toPrint/print" + stack.getIndexes() + ".png"));
        } catch (IOException e) {
            FxDialogs.showError("Ошибка", e.getMessage());
        }
    }


    public double getProgress() {
        return progress;
    }
}
