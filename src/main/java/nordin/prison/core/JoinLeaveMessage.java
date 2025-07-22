package nordin.prison.core;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class JoinLeaveMessage implements Listener {
    private final Core plugin;
    private String joinMessage;
    private String leaveMessage;
    private boolean enableBroadcast;
    private final LuckPerms luckPerms;

    public JoinLeaveMessage(Core plugin) {
        this.plugin = plugin;
        loadMessages();
        this.luckPerms = LuckPermsProvider.get();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void reloadMessages() {
        loadMessages();
    }

    private void loadMessages() {
        FileConfiguration config = plugin.getConfig();
        joinMessage = config.getString("messages.join_message");
        leaveMessage = config.getString("messages.leave_message");
        enableBroadcast = config.getBoolean("messages.enable_broadcast", true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        String prefix = getLuckPermsPrefix(player);
        String msg = joinMessage.replace("{prefix}", prefix == null ? "" : prefix)
                                .replace("{player}", player.getName());
        String coloredMsg = ChatColor.translateAlternateColorCodes('&', msg);
        if (enableBroadcast) {
            plugin.getServer().broadcastMessage(coloredMsg);
        } else {
            player.sendMessage(coloredMsg);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        String prefix = getLuckPermsPrefix(player);
        String msg = leaveMessage.replace("{prefix}", prefix == null ? "" : prefix)
                                 .replace("{player}", player.getName());
        String coloredMsg = ChatColor.translateAlternateColorCodes('&', msg);
        if (enableBroadcast) {
            plugin.getServer().broadcastMessage(coloredMsg);
        } else {
            player.sendMessage(coloredMsg);
        }
    }

    private String getLuckPermsPrefix(Player player) {
        try {
            User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
            String prefix = user.getCachedData().getMetaData().getPrefix();
            if (prefix != null) {
                return ChatColor.translateAlternateColorCodes('&', prefix);
            }
        } catch (Exception ignored) { }
        return "";
    }
}