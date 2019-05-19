package bsc.words;

import java.util.Comparator;

public class WordDistanceComparator implements Comparator<Word> {

    @Override
    public int compare(Word w1, Word w2) {
        if (w1.score > w2.score) {
            return -1;
        } else if (w1.score < w2.score) {
            return 1;
        }

        return 0;
    }
}
