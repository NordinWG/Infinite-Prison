package nordin.prison.core;

import nordin.prison.core.Prison.Backpack;
import nordin.prison.core.Prison.BackpackManager;
import nordin.prison.core.Prison.Commands.BackpackCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class Core extends JavaPlugin implements Listener, CommandExecutor {
    private FileConfiguration config;
    private FileConfiguration dataConfig;
    private JoinLeaveMessage customMessages;
    private ChatFormat chatFormatter;
    private Commands customCommands;
    private Deaths Deaths;

    // Use EU/UK formatting for numbers (dots as thousands separator)
    private final NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        loadConfigMessages();
        getServer().getPluginManager().registerEvents(this, this);
        customMessages = new JoinLeaveMessage(this);
        chatFormatter = new ChatFormat(this);
        getServer().getPluginManager().registerEvents(chatFormatter, this);
        customCommands = new Commands(this);
        getCommand("helpme").setExecutor(customCommands);
        getCommand("rules").setExecutor(customCommands);
        getCommand("spawn").setExecutor(customCommands);
        getCommand("website").setExecutor(customCommands);
        getCommand("discord").setExecutor(customCommands);
        getCommand("cleardroppd").setExecutor(customCommands);

        BackpackManager backpackManager = new BackpackManager();

        getCommand("backpack").setExecutor(new BackpackCommand(backpackManager));

        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (!(event.getPlayer() instanceof Player)) return;
                if (!event.getView().getTitle().equals("Backpack")) return;

                Player player = (Player) event.getPlayer();
                Backpack backpack = backpackManager.getBackpack(player.getUniqueId());
                backpack.saveInventory(event.getInventory());
            }
        }, this);
    }


    private void loadConfigMessages() {
        if (!config.contains("messages")) {
            config.createSection("messages");
        }
    }

    private void saveConfigAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    config.save(new File(getDataFolder(), "config.yml"));
                } catch (IOException e) {
                }
            }
        }.runTaskAsynchronously(this);
    }

    public void reloadPlugin() {
        reloadConfig();
        config = getConfig();
        loadConfigMessages();
        customMessages.reloadMessages();
        chatFormatter.reloadFormat();
        customCommands.reloadMessages();
    }
}