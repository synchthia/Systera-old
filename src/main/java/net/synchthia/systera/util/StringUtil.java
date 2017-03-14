package net.synchthia.systera.util;

import org.bukkit.ChatColor;

/**
 * @author Laica-Lunasys
 */

public class StringUtil {
    public static String build(String[] strings, String separator) {
        return build(strings, separator, 0);
    }

    public static String build(String[] strings, String separator, int start) {
        if (strings != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = start; i < strings.length; i++) {
                if (i > start)
                    sb.append(separator);
                sb.append(strings[i]);
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public static String coloring(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String someLineToOneLine(String[] strings) {
        String string = "";
        for (int i = 0; i < strings.length; i++) {
            string = string + strings[i] + "\n";
        }

        return string;
    }
}