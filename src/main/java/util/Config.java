package util;

import dto.JobDTO;
import lombok.Getter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for loading configuration.
 * Example of this file in src/main/resources/config.xml
 */
public class Config {

    private final static Logger LOGGER = Logger.getLogger(Config.class);

    private final static String PATH_TO_CONFIG_FILE = "/config.xml";

    private static final Properties props = new Properties();

    @Getter
    private static int reportCount = 1;

    @Getter
    private static String storagePath;

    @Getter
    private static String jenkinsUrl;

    @Getter
    private static String jenkinsBasicAuth;

    @Getter
    private static boolean hasJenkins = false;

    @Getter
    private static Set<JobDTO> jenkinsJobs;



    static {
        loadProperties();
    }

    public static String getProperty(String name) {
        if (!props.containsKey(name)) {
            LOGGER.error("Config option with name - " + name + " does not exists.");
        }
        return props.getProperty(name);
    }

    private static void loadProperties() {
        try {
            props.loadFromXML(Config.class.getResourceAsStream(PATH_TO_CONFIG_FILE));
            reportCount = Integer.parseInt(getProperty("reportCount"));
            storagePath = getProperty("storage");

            if (props.containsKey("jenkinsUrl")) {
                hasJenkins = true;
                jenkinsUrl = getProperty("jenkinsUrl");
                jenkinsBasicAuth = getProperty("jenkinsBasicAuth");

                List<String> packages = Arrays.stream(getProperty("jPackageNames").split(",")).map(String::trim)
                        .collect(Collectors.toList());
                List<String> urls = Arrays.stream(getProperty("jPaths").split(",")).map(String::trim)
                        .collect(Collectors.toList());

                if (packages.size() != urls.size()) {
                    LOGGER.error("Count elements \"jPackageNames\" and elements \"jPaths\" in you config.xml MUST be equals. But isn't.");
                    jenkinsJobs = Collections.emptySet();
                } else {
                    jenkinsJobs = new HashSet<>();
                    for (int i = 0; i < packages.size(); i++) {
                        String url = jenkinsUrl.endsWith("/") ? jenkinsUrl : jenkinsUrl + "/";
                        url += urls.get(i).startsWith("/") ? urls.get(i).substring(1) : urls.get(i);
                        Path packagePath = Paths.get(storagePath).resolve(Paths.get(packages.get(i)));
                        JobDTO job = new JobDTO(url, packagePath);
                        jenkinsJobs.add(job);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Exception when load configuration with path " + PATH_TO_CONFIG_FILE, e);
        }
    }
}
