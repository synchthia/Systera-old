package net.synchthia.systera.util;

import net.synchthia.systera.SysteraPlugin;

/**
 * @author Laica-Lunasys
 */
public class ServerUtil {
    public static String getServerName(boolean camelize) {
        String name = SysteraPlugin.getServerName();
        if (camelize) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }

        return name;
    }
}
