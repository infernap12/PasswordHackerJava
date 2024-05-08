package hacker;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class DictionaryGenerator implements Iterator<String> {
    private final List<String> DICTIONARY;
    private int currentWordIndex = 0;
    private int currentCombination = 0;

    public DictionaryGenerator(List<String> dictionary) {
        this.DICTIONARY = dictionary;
    }

    @Override
    public boolean hasNext() {
        return !(currentWordIndex >= DICTIONARY.size() && !isCurrentWordExhausted());
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        String nextCombination = calculateCombination();
        incrementState();

        return nextCombination;
    }

    private String calculateCombination() {
        String currentWord = DICTIONARY.get(currentWordIndex);
        char[] currentCombinationArray = new char[currentWord.length()];
        int temp = this.currentCombination;
        for (int j = 0; j < currentWord.length(); j++) {
            if ((temp & 1) == 1) {
                currentCombinationArray[j] = Character.toUpperCase(currentWord.charAt(j));
            } else {
                currentCombinationArray[j] = currentWord.charAt(j);
            }
            temp >>= 1;
        }

        return new String(currentCombinationArray);
    }

    private void incrementState() {
        String currentWord = DICTIONARY.get(currentWordIndex);
        int maxCurrentWordCombinations = 1 << currentWord.length();
        this.currentCombination++;
        if (this.currentCombination >= maxCurrentWordCombinations) {
            this.currentWordIndex++;
            this.currentCombination = 0;
        }
    }

    private boolean isCurrentWordExhausted() {
        String currentWord = DICTIONARY.get(currentWordIndex);
        int maxCurrentWordCombinations = 1 << currentWord.length();
        return this.currentCombination >= maxCurrentWordCombinations;
    }
}