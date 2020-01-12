package code;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;

import javax.sound.midi.Patch;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Controller {

    @FXML
    public void onClickMethod() {
        FxDialogs.showInformation("Помощь", "%number% - для кодирования номера талона, \n%name% - для кодирования ФИО, \nлибо просто введите любой текст.");
    }

    @FXML
    public void getFile() {
        FileChooser fc = new FileChooser();
        File photo = fc.showOpenDialog(null);

        if (photo != null) {
            FxDialogs.showInformation("Файл", photo.getPath());
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
