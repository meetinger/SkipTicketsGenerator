package ru.yanchikdev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public static Controller control;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("layout.fxml"));
        Parent root = loader.load();
        control = loader.getController();
        control.updatePreview();
        primaryStage.setTitle("SkipTicketsGenerator");
        primaryStage.setScene(new Scene(root, 600, 410));
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getClassLoader().getResource("img/favicon.png"))));
        primaryStage.show();
    }

   /* public static Controller getController(){
       // return loader.getController();
    }*/

    public static void main(String[] args) {
        launch(args);
    }
}
