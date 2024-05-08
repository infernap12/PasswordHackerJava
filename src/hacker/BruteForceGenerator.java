package hacker;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BruteForceGenerator implements Iterator<String> {
    private final int maxLength;
    private int currentLength = 1;
    private final int[] currentState;
    private boolean isExhausted = false;

    // Define the character ranges
    private static final char[] PASSWORD_CHARACTERS;

    static {
        char[][] pairs = {{'a', 'z'}, {'A', 'Z'}, {'0', '9'}};
        PASSWORD_CHARACTERS = Arrays.stream(pairs)
                .flatMapToInt(x -> IntStream.rangeClosed(x[0], x[1]))
                .mapToObj(c -> "" + (char) c)
                .collect(Collectors.joining())
                .toCharArray();
    }

    public BruteForceGenerator(int maxLength) {
        this.maxLength = maxLength;
        this.currentState = new int[maxLength];
    }

    @Override
    public boolean hasNext() {
        return !isExhausted;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        String nextPassword = calculatePassword();
        calculateNext();

        return nextPassword;
    }

    private String calculatePassword() {
        char[] password = new char[currentLength];
        for (int i = 0; i < currentLength; i++) {
            password[i] = PASSWORD_CHARACTERS[currentState[i]];
        }
        return new String(password);
    }

    private void calculateNext() {
        for (int i = 0; i < maxLength; i++) {
            int plusOne = (currentState[i] + 1) % PASSWORD_CHARACTERS.length;
            currentState[i] = plusOne;

            if (plusOne != 0) {
                // We didn't wrap around to the start, so we're done.
                return;
            } else if (i + 1 == currentLength && currentLength < maxLength) {
                // We did wrap around, and it's the last character of the current length, and there's room to grow
                currentLength++;
                return;
            } else if (i + 1 == maxLength) {
                // We exhausted all possibilities.
                isExhausted = true;
            }
        }
    }
}