package bsc;

import bsc.words.Word;
import bsc.words.WordDistanceComparator;
import bsc.words.WordFrequencyComparator;
import com.google.common.collect.MinMaxPriorityQueue;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.simmetrics.StringMetric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class SpellChecker {

    public static final double MIN_SIMILARITY_SCORE = 0.7;

    private PatriciaTrie<Double> wordTrie;
    private StringMetric metric;

    public SpellChecker(Map<String, Double> dict, StringMetric metric) {
        this.metric = metric;
        wordTrie = new PatriciaTrie<>(dict);
    }

    public List<String> findSimilar(int count, String word) {
        Queue<Word> queue = MinMaxPriorityQueue
                .orderedBy(new WordDistanceComparator().thenComparing(new WordFrequencyComparator()))
                .maximumSize(count).create();

        for (Map.Entry<String, Double> entry : wordTrie.entrySet()) {
            float score = metric.compare(entry.getKey(), word);
            if (score > MIN_SIMILARITY_SCORE) {
                queue.add(new Word(entry.getKey(), score, entry.getValue()));
            }
        }

        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            result.add(queue.poll().word);
        }

        return result;
    }

}
