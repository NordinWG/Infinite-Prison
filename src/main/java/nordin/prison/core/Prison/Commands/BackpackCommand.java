// File: BackpackCommand.java
package nordin.prison.core.Prison.Commands;

import nordin.prison.core.Prison.Backpack;
import nordin.prison.core.Prison.BackpackManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackpackCommand implements CommandExecutor {

    private final BackpackManager backpackManager;

    public BackpackCommand(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Backpack backpack = backpackManager.getBackpack(player.getUniqueId());
        backpack.open(player);

        return true;
    }
}
