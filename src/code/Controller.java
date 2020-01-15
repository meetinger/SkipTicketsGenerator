package code;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;

import javax.sound.midi.Patch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Controller {

    ArrayList<Ticket> tickets = new ArrayList<>();

    @FXML
    TextField schoolField, fioField, amountField, qrcodeField;

    @FXML
    ImageView preImage;
    String schoolName;
    String fio;
    int amount;
    String qrcode;
    Image teacher;

    Ticket preView;


    public void createTicket(String index, String school, String fio, String QRCode, Image teacher){
        tickets.add(new Ticket(index, school,fio, QRCode, teacher));
    }

    public void updateVars(){
        schoolName = schoolField.getText();
        fio = fioField.getText();
        amount = Integer.parseInt(amountField.getText());
        qrcode = qrcodeField.getText();
    }


    @FXML
    public void updatePreview(){
        updateVars();
        //System.out.println("UPDATED!");
        preView = new Ticket("0", schoolName, fio, qrcode, teacher);
        //System.out.println("CREATED!");
        //FxDialogs.showInformation("Переменные", schoolName+"\n"+fio+"\n"+amount+"\n"+qrcode);
       // System.out.println("SHOWED!");
        //preImage.setImage(preView.getResultImage());
        preImage.setImage(preView.getResultScaledImage());
       // System.out.println("SETED!");
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
            teacher = new Image(photo.toURI().toString());
            //updatePreview(image);
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
        } else {
            FxDialogs.showError("Ошибка", "Директория Неверный!");
        }
    }

    
}
