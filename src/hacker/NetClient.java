package hacker;

import java.io.*;
import java.net.Socket;

public class NetClient {
    DataOutputStream outputStream;
    DataInputStream inputStream;

    public NetClient(Socket socket) {
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String sendAndReceive(String msg) {
        String response;
        try {
            outputStream.writeUTF(msg);
            response = inputStream.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
