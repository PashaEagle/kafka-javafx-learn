package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.data.Context;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Context context = Context.getInstance();
        Parent root = FXMLLoader.load(getClass().getResource("view/login.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
        context.primaryStage = primaryStage;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
