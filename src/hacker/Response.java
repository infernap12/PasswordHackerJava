package hacker;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Response) obj;
        return Objects.equals(this.result, that.result) &&
               this.elapsedTime == that.elapsedTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, elapsedTime);
    }

    @Override
    public String toString() {
        return "Response[" +
               "result=" + result + ", " +
               "elapsedTime=" + elapsedTime + ']';
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}

