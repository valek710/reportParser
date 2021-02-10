import dto.JobDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Config;

import java.lang.reflect.Method;
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

    private final Set<JobDTO> testJobDTOs = new HashSet<>(Arrays.asList(
            new JobDTO("localhost:8080/job/testPackage/job/IntegrationTests", Paths.get("home/allure/integration")),
            new JobDTO("localhost:8080/job/testPackage/job/UITests", Paths.get("home/allure/ui")),
            new JobDTO("localhost:8080/job/testPackage/job/ApiTests", Paths.get("home/allure/api"))
    ));

    @Test
    public void loadBasicConfiguration() {
        Assertions.assertEquals(5, Config.getReportCount());
        Assertions.assertEquals("home/allure/", Config.getStoragePath());
    }

    @Test
    public void loadJenkinsConfiguration() {
        Assertions.assertEquals("localhost:8080", Config.getJenkinsUrl());
        Assertions.assertEquals("abcd1234", Config.getJenkinsBasicAuth());
        Assertions.assertEquals(testJobDTOs, Config.getJenkinsJobs());
    }

    @SneakyThrows()
    @BeforeEach
    public void before() {
        Files.copy(prodFile, tempFile, REPLACE_EXISTING);
        Files.copy(testFile, prodFile, REPLACE_EXISTING);

        Method reload = Config.class.getDeclaredMethod("loadProperties");
        reload.setAccessible(true);
        reload.invoke(null);
    }

    @SneakyThrows()
    @AfterEach
    public void after() {
        Files.copy(tempFile, prodFile, REPLACE_EXISTING);
        Files.delete(tempFile);
    }
}