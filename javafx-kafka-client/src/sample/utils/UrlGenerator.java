package sample.utils;

import java.net.URI;

public class UrlGenerator {
    private static String domain = "http://localhost";
    private static String port = "8080";
    private static String apiPath = "/api";
    private static String sendPath = "/send";
    private static String messagesPath = "/messages";
    private static String logoutPath = "/logout";

    private static String getBasePath() {
        return domain + ":" + port + apiPath;
    }

    public static URI getSendMessageUrl() {
        return URI.create(getBasePath() + sendPath);
    }

    public static URI getAllMessagesUrl(String username, Integer clientPort) {
        return URI.create(getBasePath() + messagesPath + "/" + username + "?clientPort=" + clientPort);
    }

    public static URI getLogoutUrl(String username, Integer clientPort) {
        return URI.create(getBasePath() + logoutPath + "?username=" + username + "&port=" + clientPort);
    }

}
