package ru.yanchikdev;


import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import ru.yanchikdev.lib.ImageUtils;
import ru.yanchikdev.lib.MathUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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
    Slider XSlider, YSlider, iconSizeSlider, thresholdSlider;

    @FXML
    CheckBox BGRemoveCB;

    @FXML
    ImageView preImage;
    String school;
    String fio;
    int amount, xAdd, yAdd, iconSize ,BGThreshold;
    String qrcode;
    String quote;
    String path = "";
    BufferedImage icon = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);

    Boolean isBGRemove;


    Ticket preView;

    ArrayList<TicketsWorker> ticketsWorkers = new ArrayList<TicketsWorker>();
    ArrayList<StackWorker> stackWorkers = new ArrayList<StackWorker>();

    int threadsCounter = 0;
    int numThreads = Runtime.getRuntime().availableProcessors();

    Boolean isTrottlerFirst = true;

    Timer trottler = new javax.swing.Timer(50, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            if(!isTrottlerFirst){
                updateIcon();
                ((Timer) evt.getSource()).stop();
                isTrottlerFirst = true;
            }
            isTrottlerFirst = false;
        }
    });


    public void initialize() {
        XSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateIconTrottled();
        });
        YSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateIconTrottled();
        });
        iconSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateIconTrottled();
        });
        thresholdSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateIconTrottled();
        });
    }

    public void updateIconTrottled(){
        if(!trottler.isRunning()){
            trottler.start();
        }
    }

    public void updateVars() {
        school = schoolField.getText();
        fio = fioField.getText();
        amount = Integer.parseInt(amountField.getText());
        qrcode = qrcodeField.getText();
        quote = quoteField.getText();
        xAdd = (int) XSlider.getValue();
        yAdd = (int) YSlider.getValue();
        iconSize = (int) iconSizeSlider.getValue();
        isBGRemove = BGRemoveCB.isSelected();
        if(isBGRemove){
            BGThreshold = (int) thresholdSlider.getValue();
        }else {
            BGThreshold = -1;
        }
    }

    @FXML
    public void startGenTickets() {
        updateVars();
        if (path.equals("")) FxDialogs.showError("Ошибка", "Выберите директорию сохранения!");
        else {
            tickets.clear();

            numThreads = Math.min(Runtime.getRuntime().availableProcessors(), amount);

            genTickets(numThreads);

            FxDialogs.showInformation("ОK", "Талоны сохраняются\nв указанную директорию!");
        }
    }

    public void addTicketsToArray(ArrayList<Ticket> subArray) {
        this.tickets.addAll(subArray);
    }

    public void addTicketToArray(Ticket ticket) {
        this.tickets.add(ticket);
    }

    public void threadsEnd() {
        ++threadsCounter;
    }

    public void startStacksGen() {
        if (threadsCounter >= numThreads) {
            genStacks();
        }
    }

    public void genTickets(int numthreads) {
        threadsCounter = 0;

        new File(path + "/tickets/").mkdir();
        new File(path + "/toPrint/").mkdir();

        updatePreview();

        for (int i = 0; i < numthreads; ++i) {
            ticketsWorkers.add(new TicketsWorker(school, fio, qrcode, path, quote, icon, xAdd, yAdd, iconSize,BGThreshold,((i) * amount) / (numthreads) + 1, (i + 1) * amount / numthreads));
            ticketsWorkers.get(i).start();
        }

        progressBar.setVisible(true);


        Timer progressUpdater = new javax.swing.Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                double currentProgress = calcTicketsProgress();
                progressBar.setProgress(currentProgress);
                if (currentProgress >= 1) {
                    ((Timer) evt.getSource()).stop();
                    ticketsWorkers.clear();
                    System.gc();
                    System.out.println();
                }

            }
        });

        progressUpdater.start();


    }

    public void genStacks() {

        progressBar.setStyle("-fx-accent: orange;");
        Collections.sort(tickets);


        if (amount <= 12) {
            stackWorkers.add(new StackWorker(tickets, path));
            stackWorkers.get(0).start();
        } else {
            int factor = amount/(numThreads*12) + 1;

            for (int i = 0; i <= amount / (12 * factor); ++i) {
                int lastindex = Math.min((i + 1) * 12 * factor, amount - 1);
                stackWorkers.add(new StackWorker(new ArrayList<Ticket>(tickets.subList(i * 12 * factor, lastindex + 1)), path));
                stackWorkers.get(stackWorkers.size() - 1).start();
            }
        }

        progressBar.setProgress(0);

        Timer progressUpdater = new javax.swing.Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                double currentProgress = calcStacksProgress();
                progressBar.setProgress(currentProgress);
                if (currentProgress >= 1) {
                    ((Timer) evt.getSource()).stop();
                    stackWorkers.clear();
                    System.gc();
                }

            }
        });
        progressUpdater.start();
    }

    public double calcTicketsProgress() {
        int sum = 0;
        for (TicketsWorker thread : ticketsWorkers) {
            sum += thread.getProgress();
        }
        return (double) (sum) / (amount);
    }

    public double calcStacksProgress() {
        double sum = 0;
        for (StackWorker stack : stackWorkers) {
            sum += stack.getProgress();
        }
        return sum / (stackWorkers.size()-1);
    }

    @FXML
    public void updatePreview() {
        updateVars();

        preView = new Ticket(0, school, fio, qrcode, quote, icon, xAdd, yAdd, iconSize ,BGThreshold);
            setPreview();
    }

    public void setPreview(){
        preImage.setSmooth(true);
        preImage.setImage(SwingFXUtils.toFXImage(ImageUtils.resizeImage(preView.getResultImage(), (int) (preImage.getFitWidth() * 2), (int) (preImage.getFitHeight() * 2)), null));
    }

    @FXML
    public void updateIcon(){
        updateVars();
        preView.updateIcon(icon, xAdd, yAdd, iconSize, BGThreshold);
        setPreview();
    }

    @FXML
    public void showHelp() {
        FxDialogs.showInformation("Помощь", "%number% - для кодирования номера талона,\n%school% - для кодирования школы,\n%fio% - для кодирования ФИО.\n\nДанные операторы можно комбинировать:\n%school%: %number%\n%school%, %fio%\n\nТакже Вы можете ввести любой текст.", 300);
    }

    public void setProgress(double progress){
        progressBar.setProgress(progress);
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
            icon = SwingFXUtils.fromFXImage(new Image(photo.toURI().toString()), null);
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
