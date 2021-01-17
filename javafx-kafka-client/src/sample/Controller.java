package sample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    void initialize() {
        loginButton.setOnAction(actionEvent -> {
            System.out.println(usernameField.getText());
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = null;
            HttpResponse<String> response = null;
            try {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/messages/"+usernameField.getText()))
                        .timeout(Duration.ofMinutes(1))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();
            response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.statusCode());
                System.out.println(response.body());
                ObjectMapper mapper = new ObjectMapper();
                Map<String, List<Message>> map = mapper.readValue(response.body(), new TypeReference<Map<String, List<Message>>>(){});
                System.out.println(map);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
