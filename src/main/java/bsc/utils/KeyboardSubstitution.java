package bsc.utils;

import javafx.util.Pair;
import org.simmetrics.metrics.functions.Substitution;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyboardSubstitution implements Substitution {

    private static final Point center = new Point(5, 2);
    private Map<Character, Point> mapping;

    public KeyboardSubstitution(String lang) throws URISyntaxException, IOException {
        try (Stream<String> stream = Files.lines(
                Paths.get(KeyboardSubstitution.class.getClassLoader().getResource("key_map_" + lang + ".km").toURI())
        )) {
            mapping = stream.map(s -> {
                String[] entry = s.split("[ ]+");
                return new Pair<>(entry[0].charAt(0), new Point(Integer.valueOf(entry[1]), Integer.valueOf(entry[2])));
            }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        }
    }

    @Override
    public float compare(String a, int aIndex, String b, int bIndex) {
        return (float) -mapping.getOrDefault(a.charAt(aIndex), center).distance(mapping.getOrDefault(b.charAt(bIndex), center));
    }

    @Override
    public float max() {
        return 0;
    }

    @Override
    public float min() {
        return -2;
    }
}
