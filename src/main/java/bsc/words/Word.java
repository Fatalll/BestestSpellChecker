package bsc.words;

public class Word {
    public final String word;
    public final double score;
    public final double frequency;

    public Word(String word, double score, double frequency) {
        this.word = word;
        this.score = score;
        this.frequency = frequency;
    }
}
