package net.synchthia.systera.util;

/**
 * @author Laica-Lunasys
 */
public class MessageUtil {
    public static String settingMsg(String key, Boolean value) {
        String flag;
        if (value) {
            flag = "§aON";
        } else {
            flag = "§cOFF";
        }
        return String.format("§6%s: %s", key, flag);
    }
}
