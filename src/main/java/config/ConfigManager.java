package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = new Properties();

    static{
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(CONFIG_FILE);
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("Error while loading config.properties", e);
        }
    }

    public static String getProperty(String key){
        return PROPERTIES.getProperty(key);
    }

    public static void setProperty(String key, String value){
        PROPERTIES.setProperty(key, value);
        try {
            FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE);
            PROPERTIES.store(outputStream, null);
            outputStream.close();
            LOGGER.info("Property " + key + " set to " + value);
        } catch (IOException e) {
            LOGGER.error("Error while saving config.properties", e);
        }
    }

    private ConfigManager(){}
}
