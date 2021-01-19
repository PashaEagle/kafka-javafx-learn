package sample.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sample.data.Context;
import sample.dto.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateHandler implements HttpHandler {

    private final Context context = Context.getInstance();

    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = "{}";
        t.sendResponseHeaders(200, response.length());
        InputStream inputStream = t.getRequestBody();
        String body = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        context.usernameToMessagesMap = context.mapper.readValue(body, new TypeReference<Map<String, List<Message>>>() {
        });
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}