package code;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;

import javax.imageio.ImageIO;
import javax.sound.midi.Patch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            for (int i = 1; i <= amount; ++i) {
                // createTicket(String.valueOf(i), school, fio, qrcode, icon);
                writeTicketToFile(new Ticket(String.valueOf(i), school, fio, qrcode, icon));
            }
            FxDialogs.showInformation("ОK", "Талоны сохранены\nв указанную директорию!");
        }
    }

    public void writeTicketToFile(Ticket ticket){
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(ticket.getResultImage(), null), "png", new File(path+"/ticket"+ticket.getIndex()+".png"));
        } catch (IOException e) {
            FxDialogs.showError("Ошибка", e.getMessage());
        }
    }



    @FXML
    public void updatePreview(){
        updateVars();
        //System.out.println("UPDATED!");
        preView = new Ticket("0", school, fio, qrcode, icon);
        //System.out.println("CREATED!");
        //FxDialogs.showInformation("Переменные", schoolName+"\n"+fio+"\n"+amount+"\n"+qrcode);
       // System.out.println("SHOWED!");
        preImage.setImage(preView.getResultImage());
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
            icon = new Image(photo.toURI().toString());
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
            path = dirfile.getPath();
        } else {
            FxDialogs.showError("Ошибка", "Директория Неверный!");
        }
    }


}
