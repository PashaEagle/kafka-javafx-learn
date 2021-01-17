package io.reflectoring.kafka.dto;

import java.util.Objects;

public class ChatId {

    private String username1;
    private String username2;

    public ChatId() {
    }

    public ChatId(String username1, String username2) {
        this.username1 = username1;
        this.username2 = username2;
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatId chatId = (ChatId) o;
        return (username1.equals((chatId.getUsername1())) && username2.equals((chatId.getUsername2()))) ||
                (username1.equals((chatId.getUsername2())) && username2.equals((chatId.getUsername1())));
    }

    @Override
    public int hashCode() {
        return Objects.hash(username1, username2) ^ Objects.hash(username2, username1);
    }
}
