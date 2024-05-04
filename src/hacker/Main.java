package hacker;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {

    static NetClient client;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Invalid number of arguments!");
            System.exit(1);
        }
        String address = args[0];
        int port = Integer.parseInt(args[1]);
        String msg = args[2];


        try (Socket socket = new Socket(address, port)) {
            client = new NetClient(socket);
            System.out.println(client.sendAndReceive(msg));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
