package client;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.*;

public class Settings {

    private static Settings instance;

    private Map<String, String> settingsMap;

    private Settings() {
        settingsMap = new HashMap<>();
        settingsMap.put("server_ip", "localhost");
    }

    public void setSetting(String key, String value) {
        settingsMap.replace(key, value);
    }

    public String getSetting(String setting){
        return settingsMap.get(setting);
    }

    public static Settings getInstance() {
        if (isNull(instance))
            instance = new Settings();
        return instance;
    }
}
