package nordin.prison.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import nordin.prison.core.MobEconomyPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChatFormat implements Listener {
    private final MobEconomyPlugin plugin;
    private final LuckPerms luckPerms;
    private String chatFormat;

    public ChatFormat(MobEconomyPlugin plugin) {
        this.plugin = plugin;
        this.luckPerms = LuckPermsProvider.get();
        reloadFormat();
    }

    public void reloadFormat() {
        this.chatFormat = plugin.getConfig().getString("messages.chat_format", "{prefix} &3{player}: &r{message}");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.contains("[item]") || message.contains("[i]") || message.contains("[hand]")) {
            event.setCancelled(true);

            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            Component chatMessage = buildChatMessageWithItem(player, message, itemInHand);

            plugin.getServer().getOnlinePlayers().forEach(p -> p.sendMessage(chatMessage));
            plugin.getServer().getConsoleSender().sendMessage(chatMessage);

        } else {
            String prefix = getLuckPermsPrefix(player);
            String formattedMessage = chatFormat.replace("{prefix}", prefix != null ? prefix : "")
                    .replace("{player}", player.getDisplayName())
                    .replace("{message}", message);

            event.setFormat(ChatColor.translateAlternateColorCodes('&', formattedMessage));
        }
    }

    private Component buildChatMessageWithItem(Player player, String message, ItemStack item) {
        String prefix = getLuckPermsPrefix(player);

        String[] parts = message.split("\\[(item|i|hand)\\]", -1);

        TextComponent.Builder messageBuilder = Component.text();

        if (prefix != null && !prefix.isEmpty()) {
            messageBuilder.append(LegacyComponentSerializer.legacySection().deserialize(prefix));
            messageBuilder.append(Component.space());
        }

        messageBuilder.append(LegacyComponentSerializer.legacyAmpersand().deserialize("&3" + player.getDisplayName() + ": &r"));

        for (int i = 0; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                messageBuilder.append(LegacyComponentSerializer.legacyAmpersand().deserialize(parts[i]));
            }

            if (i < parts.length - 1) {
                messageBuilder.append(createItemComponent(item));
            }
        }

        return messageBuilder.build();
    }

    private Component createItemComponent(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return Component.text("[Air]")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, true);
        }

        String displayText;
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            displayText = meta.getDisplayName();
        } else {
            displayText = formatMaterialName(item.getType().name());
        }

        int amount = item.getAmount();
        String itemString = "[" + displayText + (amount > 1 ? " x" + amount : "") + "]";

        Component itemComponent = Component.text(itemString)
                .color(NamedTextColor.AQUA)
                .hoverEvent(item.asHoverEvent());

        return itemComponent;
    }

    private String formatMaterialName(String materialName) {
        String[] words = materialName.toLowerCase().split("_");
        StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) formatted.append(" ");
            formatted.append(Character.toUpperCase(words[i].charAt(0)))
                     .append(words[i].substring(1));
        }

        return formatted.toString();
    }

    private String getLuckPermsPrefix(Player player) {
        try {
            User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
            String prefix = user.getCachedData().getMetaData().getPrefix();
            if (prefix != null) {
                return ChatColor.translateAlternateColorCodes('&', prefix);
            }
        } catch (Exception ignored) {
        }
        return "";
    }
}