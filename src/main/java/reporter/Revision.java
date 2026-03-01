package reporter;

public class Revision {
    private final String timestamp;
    private final String user;

    public Revision(String timestamp, String user) {
        this.timestamp = timestamp;
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUser() {
        return user;
    }
}
