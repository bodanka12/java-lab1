import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        File rootDir;
        do {
            System.out.printf("%nEnter directory path: ");
            rootDir = new File(scan.nextLine());
        } while (!isValidDirectory(rootDir));

        System.out.printf("Starting replacements...%n%n");
        long start = System.currentTimeMillis();

        ExecutorService pool = Executors.newCachedThreadPool();
        LabTask labTask = new LabTask(rootDir, pool);
        Future<LabTaskResult> result = pool.submit(labTask);

        int res = 0;
        try {
            res = result.get().replacements();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        System.out.printf("%nTotal replacements: %d%nTotal time: %d ms%n", res, System.currentTimeMillis() - start);

        pool.shutdown();
    }

    private static boolean isValidDirectory(File file) {
        if (!file.isDirectory() || !file.exists()) {
            System.out.printf("Directory '%s' does not exist%n", file.getName());
            return false;
        }
        return true;
    }
}
