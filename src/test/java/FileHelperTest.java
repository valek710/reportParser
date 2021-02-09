import lombok.var;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.FileHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * For these tests to work, you must add the "testFile.zip" test file in src/test/resources/.
 */
public class FileHelperTest {
   private final Path path = Paths.get("newPath/");

    @Test
    public void createDirsForOnePath() throws IOException {
        try {
            FileHelper.createDirs(path);

            Assertions.assertTrue(Files.exists(path));
            Assertions.assertTrue(Files.isDirectory(path));
        } finally {
            FileUtils.forceDelete(path.toFile());
        }
    }

    @Test
    public void createDirsForTwoPath() throws IOException {
        var path1 = Paths.get("newPath1/secondLvl/");
        try {
            FileHelper.createDirs(path, path1);

            Assertions.assertTrue(Files.exists(path));
            Assertions.assertTrue(Files.isDirectory(path));
            Assertions.assertTrue(Files.exists(path1));
            Assertions.assertTrue(Files.isDirectory(path1));
        } finally {
            FileUtils.forceDelete(path.toFile());
            FileUtils.forceDelete(path1.getParent().toFile());
        }
    }

    @Test
    public void deleteDir() throws IOException {
        Files.createDirectories(path);
        try {
            FileHelper.deleteDir(path);
            Assertions.assertFalse(Files.exists(path));
        } finally {
            FileUtils.forceDeleteOnExit(path.toFile());
        }
    }

    /**
     * Change expected file size (second assert) to you file size in bytes.
     */
    @Test
    public void writeFileFromStream() throws IOException {
        var path1 = "testFile.zip";
        try(var out = new FileOutputStream(path1)) {
            var in = Paths.get("src/test/resources/testFile.zip");
            FileHelper.copy(Files.newInputStream(in), out);
            Assertions.assertTrue(Files.exists(Paths.get(path1)));
            Assertions.assertEquals(1136135, new File(path1).length());
        } catch (Exception ignored) {}
        finally {
            FileUtils.forceDeleteOnExit(new File(path1));
        }
    }

    @Test
    public void unzipFile() throws IOException {
        List<Path> resources = Files.list(Paths.get("src/test/resources")).collect(Collectors.toList());
        var path1 = Paths.get("src/test/resources/testFile.zip");

        try {
            FileHelper.unzipFile(path1);

            List<Path> list = Files.list(Paths.get("src/test/resources")).collect(Collectors.toList());
            list.removeAll(resources);
            List<File> list1 = Arrays.stream(Objects.requireNonNull(list.get(0).toFile().listFiles())).collect(Collectors.toList());
            long size = Files.walk(list.get(0)).mapToLong(p -> p.toFile().length()).sum();

            Assertions.assertTrue(list.size() > 0);
            Assertions.assertTrue(list1.size() > 0);
            Assertions.assertTrue(size > path1.toFile().length());
        } finally {
            List<Path> list = Files.list(Paths.get("src/test/resources")).collect(Collectors.toList());
            list.removeAll(resources);

            list.forEach(p -> {
                try {
                    FileUtils.forceDeleteOnExit(p.toFile());
                } catch (IOException ignored) {}
            });
        }
    }
}
