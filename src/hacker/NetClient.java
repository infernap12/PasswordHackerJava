package hacker;

import java.io.*;
import java.net.Socket;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class NetClient implements Closeable{
    Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
    int transmits = 0;


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

    public String sendAndReceive(String msg) {
        logger.info("Attempt " + transmits++ + " Sending message: " + msg);
        String response = null;
        try {
            outputStream.writeUTF(msg);
            response = inputStream.readUTF();
        } catch (IOException e) {
            logger.severe("IO Exception: " + e);
        }
        if (response == null) {
            logger.severe("No response received");
        } else {
            logger.info("Received response: " + response);
        }
        return response;
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
        inputStream.close();
    }
}
