package util;

import lombok.Getter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class for loading configuration.
 * Example of this file in src/main/resources/config.xml
 */
public abstract class Config {

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
    private static Set<String> jenkinsPackages;

    @Getter
    private static Set<String> jenkinsJobPaths;

    static {
        try {
            props.loadFromXML(Config.class.getResourceAsStream(PATH_TO_CONFIG_FILE));
            reportCount = Integer.parseInt(getProperty("reportCount"));
            storagePath = getProperty("storage");

            if (props.containsKey("jenkinsUrl")) {
                hasJenkins = true;
                jenkinsUrl = getProperty("jenkinsUrl");
                jenkinsBasicAuth = getProperty("jenkinsBasicAuth");
                jenkinsPackages = Arrays.stream(getProperty("jPackageNames").split(",")).map(String::trim)
                        .collect(Collectors.toSet());
                jenkinsJobPaths = Arrays.stream(getProperty("jPaths").split(",")).map(String::trim)
                        .collect(Collectors.toSet());
            }
        } catch (IOException e) {
            LOGGER.error("Exception when load configuration with path " + PATH_TO_CONFIG_FILE, e);
        }
    }

    public static String getProperty(String name) {
        if (!props.containsKey(name)) {
            LOGGER.error("Config option with name - " + name + " does not exists.");
        }
        return props.getProperty(name);
    }
}
