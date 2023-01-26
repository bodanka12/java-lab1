import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class LabTask implements Callable<LabTaskResult> {
    private final File file;
    private final ExecutorService pool;

    public LabTask(File file, ExecutorService pool) {
        this.file = file;
        this.pool = pool;
    }

    @Override
    public LabTaskResult call() {
        if (!file.isDirectory()) {
            int replacements = Utilities.replaceFirstAndLastWordInEveryLine(file);
            System.out.printf("File '%s' : %d replacements%n", file.getName(), replacements);
            return new LabTaskResult(replacements);
        }

        List<Future<LabTaskResult>> nestedDirectoryResults = Arrays.stream(Objects.requireNonNull(file.listFiles()))
                .filter(file1 -> file.isDirectory() || isTxtFile(file1))
                .map(file1 -> pool.submit(new LabTask(file1, pool)))
                .toList();

        return collectNestedReplacements(nestedDirectoryResults);
    }

    private boolean isTxtFile(File file) {
        return !file.isDirectory() && file.toPath().toString().endsWith(".txt");
    }

    private LabTaskResult collectNestedReplacements(List<Future<LabTaskResult>> nestedDirectoryResults) {
        int replacements = nestedDirectoryResults.stream()
                .mapToInt(value -> {
                    try {
                        return value.get().replacements();
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                })
                .sum();
        return new LabTaskResult(replacements);
    }
}
