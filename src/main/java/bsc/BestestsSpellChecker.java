package bsc;

import bsc.utils.KeyboardSubstitution;
import javafx.util.Pair;
import org.apache.commons.cli.*;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.DamerauLevenshtein;
import org.simmetrics.metrics.NeedlemanWunch;
import org.simmetrics.simplifiers.Simplifiers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.simmetrics.builders.StringMetricBuilder.with;

public class BestestsSpellChecker {

    public static final String DEFAULT_LANGUAGE      = "EN";
    public static final String DEFAULT_METRIC        = "DL";
    public static final int DEFAULT_SUGGESTIONS_COUNT = 5;

    public static final float NEEDLEMAN_WUNCH_GAP = -2.f;
    public static final float DAMERAU_LEVENSHTEIN_INSERT_DELETE_COST = 1.2f;
    public static final float DAMERAU_LEVENSHTEIN_SUBSTITUTE_COST = 1.5f;
    public static final float DAMERAU_LEVENSHTEIN_TRANSPOSE_COST = 1.0f;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("l").argName("lang").hasArg().desc("spell checker language RU or EN (default EN)").build())
                .addOption(Option.builder("m").argName("metric").hasArg().desc("spell checker metric DL or NW (default DL)").build())
                .addOption(Option.builder("c").argName("count").hasArg().desc("suggestions count for word (default 5)").build())
                .addOption(new Option("h", "help", false, "print this message"));

        String description = "This is simple spell checker for EN and RU languages. You can specify language from command line." +
                " Also you can choose a word score metric from Damerau-Levenshtein and Needleman-Wunch (based on qwerty keyboard).";

        HelpFormatter formatter = new HelpFormatter();

        String lang = DEFAULT_LANGUAGE;
        String metr = DEFAULT_METRIC;
        int suggestionsCount = DEFAULT_SUGGESTIONS_COUNT;

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.hasOption("l")) {
                lang = commandLine.getOptionValue("l");

                if (!lang.equals("RU") && !lang.equals("EN")) {
                    throw new ParseException("unknown language: " + lang);
                }
            }

            if (commandLine.hasOption("m")) {
                metr = commandLine.getOptionValue("m");

                if (!metr.equals("DL") && !metr.equals("NW")) {
                    throw new ParseException("unknown metric: " + metr);
                }
            }

            if (commandLine.hasOption("c")) {
                try {
                    suggestionsCount = Integer.valueOf(commandLine.getOptionValue("c"));

                    if (suggestionsCount < 1) {
                        throw new ParseException("suggestions count must be a positive integer bigger than 0");
                    }
                } catch (NumberFormatException e) {
                    throw new ParseException("suggestions count must be an integer");
                }
            }

            if (commandLine.hasOption("h")) {
                formatter.printHelp("Bestests Spell Check", "", options, description, true);
                return;
            }
        } catch (ParseException e) {
            System.out.print("Parse error: ");
            System.out.println(e.getMessage());
            formatter.printHelp("Bestests Spell Check", "", options, description, true);
            return;
        }

        Map<String, Double> dict;
        try (Stream<String> stream = Files.lines(
                Paths.get(Objects.requireNonNull(BestestsSpellChecker.class.getClassLoader().getResource("dict_" + lang + ".txt")).toURI())
        )) {
            dict = stream.map(s -> {
                String[] entry = s.split("[ ]+");
                return new Pair<>(entry[0], Double.valueOf(entry[1]));
            }).collect(Collectors.groupingBy(Pair::getKey, Collectors.summingDouble(Pair::getValue)));
        } catch (URISyntaxException | IOException e) {
            System.out.println("Failed to load dictionary");
            return;
        }

        StringMetric metric;
        try {
            metric = with(metr.equals("DL") ?
                    new DamerauLevenshtein(DAMERAU_LEVENSHTEIN_INSERT_DELETE_COST,
                            DAMERAU_LEVENSHTEIN_SUBSTITUTE_COST,
                            DAMERAU_LEVENSHTEIN_TRANSPOSE_COST) :

                    new NeedlemanWunch(NEEDLEMAN_WUNCH_GAP, new KeyboardSubstitution(lang)))
                    .simplify(Simplifiers.toLowerCase()).build();
        } catch (URISyntaxException | IOException e) {
            System.out.println("Failed to load keyboard mapping");
            return;
        }

        SpellChecker checker = new SpellChecker(dict, metric);

        Scanner scanner = new Scanner(System.in, "UTF-8");
        scanner.useDelimiter("\\s");

        while (scanner.hasNext()) {
            String word = scanner.next();

            List<String> suggestions = checker.findSimilar(suggestionsCount, word);

            if (suggestions.isEmpty()) {
                System.out.println("unknown word '" + word + "'");
            } else if (suggestions.get(0).equals(word)) {
                System.out.println("correct word '" + word + "'");
            } else {
                System.out.println("probably mistake in '" + word + "', suggestions:");
                for (String suggestion : suggestions) {
                    System.out.println("    " + suggestion);
                }
            }
        }
    }
}
