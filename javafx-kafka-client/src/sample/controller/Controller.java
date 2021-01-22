package sample.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sample.data.Context;
import sample.dto.Message;
import sample.utils.UrlGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Controller {

    private final Context context = Context.getInstance();
    private final HttpClient client = HttpClient.newHttpClient();

    @FXML
    private Button loginButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextField usernameField;

    @FXML
    void initialize() {
        loginButton.setOnAction(this::onLoginButtonClick);
        exitButton.setOnAction(this::onExitButtonClick);
    }

    private void onExitButtonClick(ActionEvent actionEvent) {
        System.exit(0);
    }

    private void onLoginButtonClick(ActionEvent actionEvent) {
        try {
            context.loggedUsername = usernameField.getText();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(UrlGenerator.getAllMessagesUrl(usernameField.getText(), context.httpPort))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response on login request: " + response.statusCode() + " " + response.body());
            context.usernameToMessagesMap = context.mapper.readValue(response.body(), new TypeReference<Map<String, List<Message>>>() {
            });
            openMainAppView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMainAppView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/view/app.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        context.primaryStage.setScene(new Scene(root, 800, 800));
        context.primaryStage.show();
    }
}
