package net.synchthia.systera.player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Laica-Lunasys
 */

public class PlayerSettings {
    public static Map<String, Boolean> getSettingsTemp() {
        Map<String, Boolean> settingsList = new HashMap<>();

        settingsList.put("japanize", false);

        return settingsList;
    }
}
