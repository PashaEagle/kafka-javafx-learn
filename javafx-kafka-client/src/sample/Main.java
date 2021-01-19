package sample;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.data.Context;
import sample.handler.UpdateHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Main extends Application {

    Context context = Context.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/login.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
        context.primaryStage = primaryStage;
    }


    public static void main(String[] args) throws IOException {
        Context context = Context.getInstance();
        context.httpPort = Integer.parseInt(args[0]);
        HttpServer server = HttpServer.create(new InetSocketAddress(context.httpPort), 0);
        server.createContext("/test", new UpdateHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        launch(args);
    }
}
