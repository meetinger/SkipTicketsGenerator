package code;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class Controller {
    @FXML
    private Button qrhelpbtn;

    @FXML
    public void onClickMethod(){
        FxDialogs.showInformation("Помощь", "%number% - для кодирования номера талона, \n%name% - для кодирования ФИО, \nлибо просто введите любой текст.");
    }

}