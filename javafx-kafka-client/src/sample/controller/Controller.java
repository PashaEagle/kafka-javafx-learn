package sample.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.dto.Message;

public class Controller {

    private final HttpClient client = HttpClient.newHttpClient();
    private Map<String, List<Message>> usernameToMessagesMap;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private Button logoutButton;

    @FXML
    private ListView<?> chatsListView;

    @FXML
    private Label welcomeLabel;

    @FXML
    void initialize() {
        usernameToMessagesMap = new HashMap<>();
        loginButton.setOnAction(this::onLoginButtonClick);
    }

    private void onLoginButtonClick(ActionEvent actionEvent) {
        System.out.println(usernameField.getText());
        HttpRequest request = null;
        HttpResponse<String> response = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/messages/" + usernameField.getText()))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<Message>> map = mapper.readValue(response.body(), new TypeReference<Map<String, List<Message>>>() {
            });
            System.out.println(map);

            loginButton.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/view/app.fxml"));
            loader.load();

            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
            logoutButton.setText("23");
            welcomeLabel.setText("ssdfsd");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
