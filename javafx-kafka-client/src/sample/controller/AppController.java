package sample.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import sample.data.Context;
import sample.dto.Message;
import sample.dto.SendMessageRequest;
import sample.utils.UrlGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AppController {

    private final Context context = Context.getInstance();
    private final HttpClient client = HttpClient.newHttpClient();

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Button addContactButton;
    @FXML
    private Button newMessageButton;
    @FXML
    private TextArea chatArea;
    @FXML
    private TextArea newMessageField;
    @FXML
    private TextField customRecipientField;
    @FXML
    private ListView<String> chatsListView;

    @FXML
    void initialize() {
        welcomeLabel.setText("Welcome, " + context.loggedUsername);
        logoutButton.setOnAction(this::onLogoutButtonClick);
        newMessageButton.setOnAction(this::onNewMessageButtonClick);
        addContactButton.setOnAction(this::onAddContactButtonClick);

        renderChatListView();

        chatsListView.setOnMouseClicked(actionEvent -> renderChats());

        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), actionEvent -> renderChats()));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();
    }

    private void renderChatListView() {
        chatsListView.getItems().clear();
        context.usernameToMessagesMap.keySet().forEach(username -> chatsListView.getItems().add(username));
    }

    private void renderChats() {
        if (chatsListView.getSelectionModel().getSelectedIndices().isEmpty()) return;
        context.selectedChatUsername = chatsListView.getItems().get(chatsListView.getSelectionModel().getSelectedIndices().get(0));
        if (context.selectedChatUsername.length() > 0) {
            chatArea.clear();
            List<Message> messages = context.usernameToMessagesMap.get(context.selectedChatUsername);
            if (messages == null) messages = new ArrayList<>();
            messages.forEach(message -> {
                String sender = message.getFrom().equals(context.loggedUsername) ? "you" : message.getFrom();
                String formattedTime = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(message.getTimestamp()));
                chatArea.appendText("[" + formattedTime + "] " + sender + ":\n");
                chatArea.appendText(message.getText() + "\n\n");
            });
        }
        newMessageButton.setDisable(false);
        newMessageButton.setText("Send message");
    }

    private void onLogoutButtonClick(ActionEvent actionEvent) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(UrlGenerator.getLogoutUrl(context.loggedUsername, context.httpPort))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response on logout request: " + response.statusCode() + " " + response.body());
            openLoginView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onAddContactButtonClick(ActionEvent actionEvent) {
        if (customRecipientField.getText().isEmpty()) return;
        chatsListView.getItems().add(customRecipientField.getText());
        customRecipientField.clear();
    }

    private void onNewMessageButtonClick(ActionEvent actionEvent) {
        newMessageButton.setDisable(true);
        newMessageButton.setText("Sending..");
        SendMessageRequest sendMessageRequest = new SendMessageRequest(context.loggedUsername, context.selectedChatUsername, newMessageField.getText());
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(UrlGenerator.getSendMessageUrl())
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(context.mapper.writeValueAsString(sendMessageRequest)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response on send new message request: " + response.statusCode() + " " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        newMessageField.clear();
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

