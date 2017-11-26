package net.synchthia.systera.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.PacketUtil;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Laica-Lunasys
 */
public class TabManager {
    public void setHeaderFooter(Player player) {
        String serverName = Bukkit.getServer().getServerName();
        String headerFormat = StringUtil.coloring("  &b&lSTARTAIL &8- &6&l" + serverName);
        String footerFormat = StringUtil.coloring("&astartail.io");

        PacketContainer container = SysteraPlugin.getProtocolManager()
                .createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

        // Header&Footer
        container.getChatComponents().write(0, WrappedChatComponent.fromText(headerFormat));
        container.getChatComponents().write(1, WrappedChatComponent.fromText(footerFormat));
        PacketUtil.sendPacket(player, container);
    }
}
