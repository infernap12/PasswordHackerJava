package hacker;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class NetClient implements Closeable {
    Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
    int transmits = 0;

    Gson gson = new Gson();
    DataOutputStream outputStream;
    DataInputStream inputStream;

    public NetClient(Socket socket) {
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Connected to " + socket.getInetAddress().getHostAddress());
    }

    public Response sendAndReceive(Request req) {
        String asJson = gson.toJson(req);
        logger.info("Attempt " + ++transmits + " Sending message: " + asJson);
        String strResponse = null;
        long elapsedTime = 0;
        try {
            outputStream.writeUTF(asJson);
            long startTime = System.currentTimeMillis();
            strResponse = inputStream.readUTF();
            elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("Time taken: " + elapsedTime + " ms");
        } catch (IOException e) {
            logger.severe("IO Exception: " + e);
        }
        if (strResponse == null) {
            logger.severe("No response received");
        } else {
            logger.info("Received response: " + strResponse);
        }
        Response response = gson.fromJson(strResponse, Response.class);
        response.setElapsedTime(elapsedTime);
        return response;

    }

    @Override
    public void close() throws IOException {
        outputStream.close();
        inputStream.close();
    }
}
