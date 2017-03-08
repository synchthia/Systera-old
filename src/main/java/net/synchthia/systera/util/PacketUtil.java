package net.synchthia.systera.util;

import com.comphenix.protocol.events.PacketContainer;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Laica-Lunasys
 */
public class PacketUtil {
    public static void sendPacket(Player player, PacketContainer container) {
        try {
            SysteraPlugin.getProtocolManager().sendServerPacket(player, container);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
