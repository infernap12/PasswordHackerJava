package hacker;

public final class Response {
    private final String result;
    private long elapsedTime;

    public Response(String result, long elapsedTime) {
        this.result = result;
        this.elapsedTime = elapsedTime;
    }

    public String result() {
        return result;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}

