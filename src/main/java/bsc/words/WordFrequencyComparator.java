package bsc.words;

import java.util.Comparator;

public class WordFrequencyComparator implements Comparator<Word> {

    @Override
    public int compare(Word w1, Word w2) {
        if (w1.frequency > w2.frequency) {
            return -1;
        } else if (w1.frequency < w2.frequency) {
            return 1;
        }

        return 0;
    }
}
