package sample.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Stage;
import sample.dto.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {
    private final static Context instance = new Context();

    public final ObjectMapper mapper = new ObjectMapper();

    public Integer httpPort;
    public Stage primaryStage;
    public String loggedUsername;
    public String selectedChatUsername;
    public Map<String, List<Message>> usernameToMessagesMap;

    private Context() {
        usernameToMessagesMap = new HashMap<>();
    }

    public static Context getInstance() {
        return instance;
    }

}
