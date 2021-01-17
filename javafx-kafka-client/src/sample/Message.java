package sample;

public class Message {

    private String from;
    private String to;
    private String text;
    private long timestamp;

    public Message() {
    }

    public Message(String from, String to, String text, long timestamp) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static Message fromRequest(SendMessageRequest sendMessageRequest) {
        return new Message(sendMessageRequest.getFrom(), sendMessageRequest.getTo(), sendMessageRequest.getText(), 0);
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
