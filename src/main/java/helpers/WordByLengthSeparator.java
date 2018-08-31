package helpers;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class WordByLengthSeparator {
    private static final String PATH_TO_FILE = "/Users/alivanov/Desktop/IndexWikipedia/data";

    public static void main(String[] args) {
        readFile(Paths.get(PATH_TO_FILE + "/words.txt"));
    }

    public static void readFile(Path path) {
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            stream.forEach(word -> {
                try (FileWriter writer = new FileWriter(PATH_TO_FILE + "/" + String.valueOf(word.length()) + ".txt", true)) {
                    writer.write(word + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
