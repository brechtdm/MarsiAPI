package util;

import play.Configuration;
import play.api.Play;

// https://github.com/kcoolsae/Degage/blob/3209cb5e028e72b761b66b139266c59af0db5c2d/webapp/app/controllers/util/ConfigurationHelper.java
public class ConfigurationHelper {
    private static final Configuration CONFIG;
    static {
        CONFIG = new Configuration(Play.current().configuration()); // Wraps around the Scala version, which is almost unusable in Java
    }

    private ConfigurationHelper() {

    }

    public static String getConfigurationString(String name){
        return CONFIG.getString(name);
    }
}
