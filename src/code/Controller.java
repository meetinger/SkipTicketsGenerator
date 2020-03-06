package code;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;

public class Controller {

    ArrayList<Ticket> tickets = new ArrayList<>();

    @FXML
    TextField schoolField, fioField, amountField, qrcodeField;

    @FXML
    ImageView preImage;
    String school;
    String fio;
    int amount;
    String qrcode;
    String path = "";
    Image icon;

    Ticket preView;

    public void createTicket(String index, String school, String fio, String QRCode, Image icon){
        tickets.add(new Ticket(index, school,fio, QRCode, icon));
    }

    public void updateVars(){
        school = schoolField.getText();
        fio = fioField.getText();
        amount = Integer.parseInt(amountField.getText());
        qrcode = qrcodeField.getText();
    }

    @FXML
    public void genTickets(){
        updateVars();
        if(path.equals("")) FxDialogs.showError("Ошибка", "Выберите директорию сохранения!");
        else {
            int cores = Runtime.getRuntime().availableProcessors();

            if(cores<=amount){
                startGen(cores);
            }else{
                startGen(cores);
            }

            FxDialogs.showInformation("ОK", "Талоны сохранены\nв указанную директорию!");
        }
    }

    public void startGen(int numthreads){
        ArrayList<Worker> threads = new ArrayList<>();

        for(int i = 0; i < numthreads; ++i){
            threads.add(new Worker(school, fio, qrcode, path, icon, ((i)*amount)/(numthreads)+1, (i+1)*amount/numthreads));
            threads.get(i).start();

        }
    }


    @FXML
    public void updatePreview(){
        updateVars();

        preView = new Ticket("0", school, fio, qrcode, icon);
        preImage.setImage(SwingFXUtils.toFXImage(preView.getResultImage(),null));
    }


    @FXML
    public void showHelp() {
        FxDialogs.showInformation("Помощь", "%number% - для кодирования номера талона, \n%name% - для кодирования ФИО, \nлибо просто введите любой текст.");
    }

    @FXML
    public void getFile() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(//
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
