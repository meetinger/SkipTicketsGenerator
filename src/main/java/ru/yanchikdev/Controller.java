package ru.yanchikdev;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import ru.yanchikdev.lib.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class Controller {

    ArrayList<Ticket> tickets = new ArrayList<Ticket>();

    @FXML
    TextField schoolField, fioField, amountField, qrcodeField, quoteField;

    @FXML
    ProgressBar progressBar;

    @FXML
    ImageView preImage;
    String school;
    String fio;
    int amount;
    String qrcode;
    String quote;
    String path = "";
    Image icon = SwingFXUtils.toFXImage(new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB), null);

    Ticket preView;
    ArrayList<Worker> threads = new ArrayList<Worker>();

    int threadsCounter = 0;
    int numThreads = 0;

    /*public void createTicket(String index, String school, String fio, String QRCode, Image icon) {
        tickets.add(new Ticket(index, school, fio, QRCode, icon));
    }*/


    public void updateVars() {
        school = schoolField.getText();
        fio = fioField.getText();
        amount = Integer.parseInt(amountField.getText());
        qrcode = qrcodeField.getText();
        quote = quoteField.getText();
    }

    @FXML
    public void genTickets() {
        updateVars();
        if (path.equals("")) FxDialogs.showError("Ошибка", "Выберите директорию сохранения!");
        else {

            numThreads = Math.min(Runtime.getRuntime().availableProcessors(), amount);

            startGen(numThreads);

            FxDialogs.showInformation("ОK", "Талоны сохраняются\nв указанную директорию!");
        }
    }

    public void addTicketsToArray(ArrayList<Ticket> subArray) {
        this.tickets.addAll(subArray);
    }

    public void addTicketToArray(Ticket ticket) {
        this.tickets.add(ticket);
    }

    public void threadsEnd(){
        ++threadsCounter;
    }

    public void startStacksGen(){
        if (threadsCounter >= numThreads){
            genStacks();
        }
    }

    public void startGen(int numthreads) {
        threadsCounter = 0;

        new File(path + "/tickets/").mkdir();
        new File(path + "/toPrint/").mkdir();

        updatePreview();

        for (int i = 0; i < numthreads; ++i) {
            threads.add(new Worker(school, fio, qrcode, path, quote, icon, ((i) * amount) / (numthreads) + 1, (i + 1) * amount / numthreads));
            threads.get(i).start();
        }

        progressBar.setVisible(true);


        Timer progressUpdater = new javax.swing.Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                double currentProgress = calcProgress();
                progressBar.setProgress(currentProgress);
                if (currentProgress >= 1) {
                    ((Timer) evt.getSource()).stop();
                    threads.clear();
                    System.gc();
                }

            }
        });

        progressUpdater.start();

       /* try {
            for (int i = 0; i < numthreads; ++i) {
                threads.get(i).join();
            }
        } catch (Exception e) {

        }

        genStacks();*/

    }

    public void genStacks() {
        progressBar.setStyle("-fx-accent: orange;");
        progressBar.setProgress(0);
        Collections.sort(tickets);
        if (tickets.size() < 12) {
            try {
                TicketStack tmp = new TicketStack(tickets);
                ImageIO.write(tmp.getResult(), "png", new File(path + "/tickets/print" + tmp.getIndexes() + ".png"));
                progressBar.setProgress(1);
            } catch (Exception e) {

            }
        } else {
            for (int i = 0; i < ((tickets.size() - 1) / 12); ++i) {
                try {
                    int lastindex = Math.min((i + 1) * 12, tickets.size() - 1);
                    TicketStack tmp = new TicketStack(new ArrayList<Ticket>(tickets.subList(i * 12, lastindex)));
                    ImageIO.write(tmp.getResult(), "png", new File(path + "/toPrint/print" + tmp.getIndexes() + ".png"));
                } catch (IOException e) {
                    FxDialogs.showError("Ошибка", e.getMessage());
                }
                progressBar.setProgress((double) (i+1)/((double)(tickets.size() -1 ) / 12));
            }
        }
    }

    public double calcProgress() {
        int sum = 0;
        for (Worker thread : threads) {
            sum += thread.getProgress();
        }
        return (double) (sum) / (amount);
    }


    @FXML
    public void updatePreview() {
        updateVars();

        preView = new Ticket("0", school, fio, qrcode, quote, icon);
        preImage.setSmooth(true);
        /*preImage.setFitWidth(preView.getResultImage().getWidth()/5);
        preImage.setFitHeight(preView.getResultImage().getHeight()/5);*/
        preImage.setImage(SwingFXUtils.toFXImage(ImageUtils.resizeImage(preView.getResultImage(), (int) (preImage.getFitWidth() * 2), (int) (preImage.getFitHeight() * 2)), null));
    }


    @FXML
    public void showHelp() {
        FxDialogs.showInformation("Помощь", "%number% - для кодирования номера талона,\n%school% - для кодирования школы,\n%fio% - для кодирования ФИО.\n\nДанные операторы можно комбинировать:\n%school%: %number%\n%school%, %fio%\n\nТакже Вы можете ввести любой текст.", 300);
    }

    @FXML
    public void getFile() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("GIF", "*.gif")
        );
        File photo = fc.showOpenDialog(null);

        if (photo != null) {
            FxDialogs.showInformation("Файл", photo.getPath());
            icon = new Image(photo.toURI().toString());
            updatePreview();
        } else {
            FxDialogs.showError("Ошибка", "Файл Неверный!");
        }
    }

    @FXML
    public void getDir() {
        DirectoryChooser dc = new DirectoryChooser();
        File dirfile = dc.showDialog(null);

        if (dirfile != null) {
            FxDialogs.showInformation("Директория", dirfile.getPath());
            path = dirfile.getPath();
        } else {
            FxDialogs.showError("Ошибка", "Директория Неверный!");
        }
    }


}
