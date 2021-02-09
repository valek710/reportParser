import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import util.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ConfigTest {
    private final Path testFile = Paths.get("src/test/resources/config.xml");
    private final Path prodFile = Paths.get("src/main/resources/config.xml");
    private final Path tempFile = Paths.get("src/test/resources/config1.xml");

    @Test
    public void loadBasicConfiguration() {
        Assertions.assertEquals(5, Config.getReportCount());
        Assertions.assertEquals("home/allure/", Config.getStoragePath());
    }

    @Test
    public void loadJenkinsConfiguration() {
        Set<String> packages = new HashSet<>(Arrays.asList("api", "integration", "ui"));
        Set<String> paths = new HashSet<>(Arrays.asList("job/testPackage/job/ApiTests",
                "job/testPackage/job/IntegrationTests", "job/testPackage/job/UITests"));

        Assertions.assertEquals("localhost:8080", Config.getJenkinsUrl());
        Assertions.assertEquals("abcd1234", Config.getJenkinsBasicAuth());
        Assertions.assertEquals(packages, Config.getJenkinsPackages());
        Assertions.assertEquals(paths, Config.getJenkinsJobPaths());
    }

    @SneakyThrows(IOException.class)
    @BeforeEach
    public void before() {
        Files.copy(prodFile, tempFile, REPLACE_EXISTING);
        Files.copy(testFile, prodFile, REPLACE_EXISTING);
    }

    @SneakyThrows(IOException.class)
    @AfterEach
    public void after() {
        Files.copy(tempFile, prodFile, REPLACE_EXISTING);
        Files.delete(tempFile);
    }
}