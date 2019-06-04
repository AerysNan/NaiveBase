package connection;

public class Context {
    public String username;
    public String databaseName;

    public Context(String username, String databaseName) {
        this.username = username;
        this.databaseName = databaseName;
    }

    @Override
    public String toString() {
        return username + '@' + databaseName;
    }
}
