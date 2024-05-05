package hacker;

import javax.annotation.processing.Filer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;




public class Main {
    static Logger parentLogger = Logger.getLogger(Main.class.getPackageName());
    static Logger logger = Logger.getLogger(Main.class.getCanonicalName());

    static File dictionary;

    static {
        try {
            parentLogger.setLevel(Level.INFO);
            FileHandler fh = new FileHandler("hacker.log");
            fh.setFormatter(new SimpleFormatter());
            parentLogger.addHandler(fh);

            dictionary = new File("passwords.txt");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not create dictionary", e);
            logger.info(dictionary.getAbsolutePath());
        }
    }

    static NetClient client;
    private static String pw = null;

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

            logger.info("attempting dictionary read");
            List<String> lines;
            try (BufferedReader br = new BufferedReader(new FileReader(dictionary))) {
                lines = br.lines().toList();
            }

            // for each line
//            dictionaryAttack(lines);
            optimizedDictionaryAttack(lines);
            client.close();
            client = null;

            System.out.println(pw);


        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Unknown host", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "I/O exception", e);
        }


    }

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
}
