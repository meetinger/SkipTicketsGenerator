package ru.yanchikdev;


import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import ru.yanchikdev.lib.ImageUtils;
import ru.yanchikdev.lib.MathUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
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
    Label progressLbl;

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
    ArrayList<GenerationBound> generationBounds = new ArrayList<GenerationBound>();

    int ticketsWorkerCounter = 0;
    int stackWorkersCounter = 0;
    int numThreads = Runtime.getRuntime().availableProcessors();
    int stackWorkersAmount = numThreads;
    int generationBoundIndex = 0;
    final int ticketsStepAmount = 120;

    ThrottleFunction updateIconThrottled = new ThrottleFunction((arg)->{updateIcon();}, 50);

    Timer progressUpdater = new Timer(100, evt -> {});
    double ticketsProgress = 0;
    double stackProgress = 0;
    double progress = 0;

    public void initialize() {
        XSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateIconThrottled();
        });
        YSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateIconThrottled();
        });
        iconSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateIconThrottled();
        });
        thresholdSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateIconThrottled();
        });
    }

    public void updateIconThrottled(){
        updateIconThrottled.run();
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
    public void startGeneration() {
        updateVars();
        if (path.equals("")) {
            FxDialogs.showError("Ошибка", "Выберите директорию сохранения!");
            return;
        }
        tickets.clear();
        generationBoundIndex = 0;
        generationBounds.clear();

        numThreads = Math.min(Runtime.getRuntime().availableProcessors(), amount);
//        genTickets(numThreads);
        FxDialogs.showInformation("ОK", "Талоны сохраняются\nв указанную директорию!");


        for(int i = 1; i < amount/ticketsStepAmount+2; ++i){
            int leftBound = (i-1)*ticketsStepAmount;
            int rightBound = Math.min(i*ticketsStepAmount, amount);
            GenerationBound bound = new GenerationBound(leftBound, rightBound);
            generationBounds.add(bound);
        }
        GenerationBound bound = generationBounds.get(0);
        genTickets(numThreads, bound.getLeftBound(), bound.getRightBound());
//        System.out.println(generationBounds);
    }




    public void addTicketsToArray(ArrayList<Ticket> subArray) {
        this.tickets.addAll(subArray);
    }

    public void addTicketToArray(Ticket ticket) {
        this.tickets.add(ticket);
    }

    public void ticketsWorkerEnd() {
        ++ticketsWorkerCounter;
        if (ticketsWorkerCounter >= numThreads) {
            GenerationBound bound = generationBounds.get(Math.min(generationBoundIndex, generationBounds.size()-1));
            ticketsProgress += calcTicketsProgress();
            genStacks(numThreads, bound.getLeftBound(), bound.getRightBound());
            ticketsWorkerCounter = 0;
        }

    }

    public void stackWorkerEnd(){
        ++stackWorkersCounter;
        if(generationBoundIndex >= generationBounds.size()){
//            FxDialogs.showInformation("OK", "Талоны сохранены в указанную директорию!");
            return;
        }
        if (stackWorkersCounter >= stackWorkersAmount){
            GenerationBound bound = generationBounds.get(generationBoundIndex);
            genTickets(numThreads, bound.getLeftBound(), bound.getRightBound());
            stackWorkersCounter = 0;
            Platform.runLater(()->{progressLbl.setText(generationBoundIndex + "/" +generationBounds.size());});
            ++generationBoundIndex;
        }
    }

    public void genTickets(int numThreads, int leftBound, int rightBound) {
        progressUpdater.stop();
        tickets.clear();
        ticketsWorkers.clear();
        ticketsWorkerCounter = 0;

        new File(path + "/tickets/").mkdir();
        new File(path + "/toPrint/").mkdir();

        updatePreview();

        for (int i = 0; i < numThreads; ++i) {
            ticketsWorkers.add(new TicketsWorker(school, fio, qrcode, path, quote, icon, xAdd, yAdd, iconSize, BGThreshold,
                    ((i) * (rightBound - leftBound)) / (numThreads) + leftBound + 1, (i + 1) * (rightBound - leftBound) / numThreads + leftBound));
            ticketsWorkers.get(i).start();
        }

        progressBar.setStyle("");

        progressBar.setVisible(true);


        this.progressUpdater = new javax.swing.Timer(100, evt -> {
            double currentProgress = calcTicketsProgress();
            progressBar.setProgress(currentProgress);
            if (currentProgress >= 1) {
                ((Timer) evt.getSource()).stop();
                ticketsWorkers.clear();
            }
        });

        progressUpdater.start();

//        for(TicketsWorker i : ticketsWorkers){
//            try {
//                i.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }

    public void genStacks(int numThreads, int leftBound, int rightBound) {
        progressUpdater.stop();
        stackWorkers.clear();
        progressBar.setStyle("-fx-accent: orange;");
        Collections.sort(tickets);

        int amount = rightBound-leftBound;


        int ticketsPerWorker = MathUtils.lcm(numThreads, 12);

        if (amount <= ticketsPerWorker) {
            stackWorkers.add(new StackWorker(tickets, path));
            stackWorkers.get(0).start();
            stackWorkersAmount = 1;
        } else {
            stackWorkersAmount = 0;

            for(int i = 1; i < amount/ticketsPerWorker + 1; ++i) {
                int lastIndex = Math.min(i * ticketsPerWorker, amount);
                stackWorkers.add(new StackWorker(tickets.subList((i - 1) * ticketsPerWorker, lastIndex), path));
                stackWorkers.get(stackWorkers.size() - 1).start();
                ++stackWorkersAmount;
            }
        }

        progressBar.setProgress(0);

        this.progressUpdater = new javax.swing.Timer(100, new ActionListener() {
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
        double sumProgress = 0;
        double sumTickets = 0;
        for (TicketsWorker thread : ticketsWorkers) {
            sumProgress += thread.getProgress();
            sumTickets += thread.getTicketsAmount();
        }
        return sumProgress / sumTickets;
    }

    public double calcStacksProgress() {
        double sumProgress = 0;
        double sumTickets = 0;
        for (StackWorker thread : stackWorkers) {
            sumProgress += thread.getProgress();
            sumTickets += thread.getTicketsAmount();
        }
        return sumProgress / sumTickets;
    }

    @FXML
    public void updatePreview() {
        updateVars();

        preView = new Ticket(0, school, fio, qrcode, quote, icon, xAdd, yAdd, iconSize, BGThreshold);
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
        File dirFile = dc.showDialog(null);

        if (dirFile != null) {
            FxDialogs.showInformation("Директория", dirFile.getPath());
            path = dirFile.getPath();
        } else {
            FxDialogs.showError("Ошибка", "Директория Неверный!");
        }
    }


}
