package nordin.prison.core;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Commands implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private List<String> helpmeMessages;
    private String killmeMessage;
    private List<String> rulesMessages;

    public Commands(JavaPlugin plugin) {
        this.plugin = plugin;
        new Random();
        reloadMessages();
    }

    public void reloadMessages() {
        this.helpmeMessages = Arrays.asList(
            plugin.getConfig().getString("messages.helpme.normal_header", "&lNormal Commands:"),
            plugin.getConfig().getString("messages.helpme.discord", "&9/discord"),
            plugin.getConfig().getString("messages.helpme.website", "&9/website"),
            plugin.getConfig().getString("messages.helpme.helpme", "&9/helpme"),
            plugin.getConfig().getString("messages.helpme.spawn", "&9/spawn"),
            plugin.getConfig().getString("messages.helpme.rules", "&9/rules"),
            plugin.getConfig().getString("messages.helpme.fun_header", "&lFun Commands:")
        );
        this.killmeMessage = plugin.getConfig().getString("messages.killme.message", "&fOwch.");
        this.rulesMessages = Arrays.asList(
            plugin.getConfig().getString("messages.rules.header", "&lRules:"),
            plugin.getConfig().getString("messages.rules.hack", "- No hacking outside Anarchy"),
            plugin.getConfig().getString("messages.rules.lag", "- Don't try to lag/crash the server"),
            plugin.getConfig().getString("messages.rules.common", "- Use common sense"),
            plugin.getConfig().getString("messages.rules.fun", "- Have fun")
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "helpme":
            case "rules":
            case "spawn":
            case "website":
            case "discord":
            case "cleardroppd": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player_only", "&cThis command can only be used by players!")));
                    return true;
                }
                Player player = (Player) sender;
                switch (command.getName().toLowerCase()) {
                    case "helpme":
                        for (String message : helpmeMessages) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                        return true;
                    case "rules":
                        for (String message : rulesMessages) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                        return true;
                    case "spawn":
                        teleportToFixedSpawn(player);
                        return true;
                    case "website":
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&9&lCheck out our website: &b&lfinites.org"));
                        return true;
                    case "discord":
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&9&lJoin our Discord: &b&ldiscord.gg/q9UUbxkVqc"));
                        return true;
                    case "cleardroppd":
                        if (!player.hasPermission("sk.cleardroppd")) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.getConfig().getString("messages.no_permission", "&cYou do not have permission to use this command!")));
                            return true;
                        }
                        clearDroppedItemsAndBroadcast();
                        return true;
                    default:
                        return false;
                }
            }
        }
        return false;
    }

    private void teleportToFixedSpawn(Player player) {
        double x = 0.5;
        double z = 0.5;
        for (int y = -1; y <= 320; y++) {
            Location baseLoc = new Location(player.getWorld(), x, y, z);
            Location headLoc = new Location(player.getWorld(), x, y + 1, z);
            Location aboveLoc = new Location(player.getWorld(), x, y + 2, z);
            if (baseLoc.getBlock().isSolid() && headLoc.getBlock().isEmpty() && aboveLoc.getBlock().isEmpty()) {
                player.teleport(headLoc);
                return;
            }
        }
    }

    private void broadcastCleared(int droppedCount) {
        String msg = plugin.getConfig().getString("messages.cleardrops_done",
                "&7Killed &6{count} &7dropped items.");
        msg = msg.replace("{count}", String.valueOf(droppedCount));
        plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    private void clearDroppedItemsAndBroadcast() {
        int droppedCount = clearDroppedItems();
        broadcastCleared(droppedCount);
    }

    private int clearDroppedItems() {
        int droppedCount = 0;
        for (World world : plugin.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item) {
                    droppedCount++;
                    entity.remove();
                }
            }
        }
        return droppedCount;
    }

    private String escapeMiniMessage(String text) {
        return text.replace("<", "&lt;").replace(">", "&gt;");
    }
}