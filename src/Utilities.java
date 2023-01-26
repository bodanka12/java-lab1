import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class Utilities {
    private Utilities() {
    }

    public static int replaceFirstAndLastWordInEveryLine(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            String newText = lines.stream()
                    .map(s -> s.replaceAll("^(\\W*)(\\w+)(.*\\b)(\\w+)(\\W*)$", "$1$4$3$2$5"))
                    .collect(Collectors.joining(System.lineSeparator()));
            Files.writeString(file.toPath(), newText, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return lines.size();
        } catch (IOException e) {
            throw new RuntimeException("Replacing error in '%s' : %s".formatted(file.getAbsolutePath(), e.getMessage()));
        }
    }
}
