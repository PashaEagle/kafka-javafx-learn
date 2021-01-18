package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.data.Context;
import sample.dto.Message;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AppController {

    private Context context;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane welcomeText;

    @FXML
    private Button logoutButton;

    @FXML
    private TextField newMessageRecipient;

    @FXML
    private ListView<String> chatsListView;

    @FXML
    private TextArea chatArea;

    @FXML
    private Button newMessageButton;

    @FXML
    private TextArea newMessageField;

    @FXML
    private Label welcomeLabel;

    @FXML
    void initialize() {
        context = Context.getInstance();
        welcomeLabel.setText("Welcome, " + context.loggedUsername);
        logoutButton.setOnAction(this::onLogoutButtonClick);
        context.usernameToMessagesMap.keySet().forEach(username -> {
            chatsListView.getItems().add(username);
        });

        chatsListView.setOnMouseClicked(actionEvent -> {
            context.selectedChatUsername = chatsListView.getItems().get(chatsListView.getSelectionModel().getSelectedIndices().get(0));
            if (context.selectedChatUsername.length() > 0) {
                chatArea.clear();
                List<Message> messages = context.usernameToMessagesMap.get(context.selectedChatUsername);
                messages.forEach(message -> {
                    String sender = message.getFrom().equals(context.loggedUsername) ? "you" : message.getFrom();
                    String formattedTime = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date (message.getTimestamp()));
                    chatArea.appendText("[" + formattedTime + "] " + sender + ":\n");
                    chatArea.appendText(message.getText() + "\n\n");
                });

            }
        });
    }

    private void onLogoutButtonClick(ActionEvent actionEvent) {
        try {
            openLoginView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/view/login.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        context.primaryStage.setScene(new Scene(root, 800, 500));
        context.primaryStage.show();
    }
}

