package sample.data;

public class Context {
    private final static Context instance = new Context();

    public static Context getInstance() {
        return instance;
    }

}
