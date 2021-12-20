package ru.yanchikdev;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StackWorker extends Thread {

    List<Ticket> stack;
    String path;
    int progress;

    public StackWorker(List<Ticket> stack, String path) {
        this.stack = stack;
        this.path = path;
    }

    @Override
    public void run() {

        if (stack.size() <= 12) {
            TicketStack tmp = new TicketStack(stack);
            writeStackToFile(tmp);
        }
        for (int i = 1; i < stack.size() / 12 + 2; ++i) {
            int lastIndex = Math.min(i * 12, stack.size());
            if(stack.subList((i - 1) * 12, lastIndex).isEmpty()){
                break;
            }
            TicketStack tmp = new TicketStack(stack.subList((i - 1) * 12, lastIndex));
            writeStackToFile(tmp);

            progress+=12;
        }
        Main.control.stackWorkerEnd();
    }


    private void writeStackToFile(TicketStack stack) {
        try {
            ImageIO.write(stack.getResultImage(), "png", new File(path + "/toPrint/print" + stack.getIndexes() + ".png"));
        } catch (IOException e) {
            FxDialogs.showError("Ошибка", e.getMessage());
        }
    }

    public int getTicketsAmount(){
        return stack.size();
    }

    public int getProgress() {
        return progress;
    }
}
