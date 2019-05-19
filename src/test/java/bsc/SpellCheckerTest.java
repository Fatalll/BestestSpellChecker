package bsc;

import javafx.util.Pair;
import org.junit.Test;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.DamerauLevenshtein;
import org.simmetrics.simplifiers.Simplifiers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.simmetrics.builders.StringMetricBuilder.with;

public class SpellCheckerTest {

    private SpellChecker checker;

    public SpellCheckerTest() {
        Map<String, Double> dict;
        try (
                Stream<String> stream = Files.lines(
                        Paths.get(Objects.requireNonNull(BestestsSpellChecker.class.getClassLoader().getResource("dict_EN.txt")).toURI())
                )) {
            dict = stream.map(s -> {
                String[] entry = s.split("[ ]+");
                return new Pair<>(entry[0], Double.valueOf(entry[1]));
            }).collect(Collectors.groupingBy(Pair::getKey, Collectors.summingDouble(Pair::getValue)));
        } catch (URISyntaxException | IOException e) {
            System.out.println("Failed to load dictionary");
            return;
        }

        StringMetric metric = with(new DamerauLevenshtein(1.f, 1.5f, 1.f))
                .simplify(Simplifiers.toLowerCase()).build();

        checker = new SpellChecker(dict, metric);
    }


    @Test
    public void findCorrectWord() {
        List<String> suggestions = checker.findSimilar(5, "somebody");

        assertFalse(suggestions.isEmpty());
        assertEquals("somebody", suggestions.get(0));
    }

    @Test
    public void findUnknownWord() {
        List<String> suggestions = checker.findSimilar(5, "vsdzvbuhkad");

        assertTrue(suggestions.isEmpty());
    }

    @Test
    public void findWordWithMistake() {
        List<String> suggestions = checker.findSimilar(5, "somebady");

        assertFalse(suggestions.isEmpty());
        assertEquals("somebody", suggestions.get(0));
    }

    @Test
    public void findWordWithoutCharacter() {
        List<String> suggestions = checker.findSimilar(5, "somebdy");

        assertFalse(suggestions.isEmpty());
        assertEquals("somebody", suggestions.get(0));
    }

    @Test
    public void findWordWithAdditionalCharacter() {
        List<String> suggestions = checker.findSimilar(5, "someboody");

        assertFalse(suggestions.isEmpty());
        assertEquals("somebody", suggestions.get(0));
    }

    @Test
    public void findWordComplex() {
        List<String> suggestions = checker.findSimilar(5, "samebooy");

        assertFalse(suggestions.isEmpty());
        assertEquals("somebody", suggestions.get(0));
    }
}