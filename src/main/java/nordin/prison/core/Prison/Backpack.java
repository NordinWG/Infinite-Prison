package nordin.prison.core.Prison;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Backpack {
    private int capacity = 27; // Number of slots (default 1 row chest)
    private final ItemStack[] contents;

    public Backpack() {
        this.contents = new ItemStack[capacity];
    }

    public boolean addItem(ItemStack item) {
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null) {
                contents[i] = item;
                return true;
            }
        }
        return false; // No space
    }

    public boolean removeItem(Material material, int amount) {
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack != null && stack.getType() == material) {
                if (stack.getAmount() >= amount) {
                    stack.setAmount(stack.getAmount() - amount);
                    if (stack.getAmount() <= 0) contents[i] = null;
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public int getCapacity() {
        return capacity;
    }

    public void upgradeCapacity(int newCapacity) {
        if (newCapacity <= capacity) return;

        ItemStack[] newContents = new ItemStack[newCapacity];
        System.arraycopy(contents, 0, newContents, 0, capacity);
        capacity = newCapacity;
        System.arraycopy(newContents, 0, contents, 0, newContents.length);
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(player, capacity, "Backpack");
        gui.setContents(contents);
        player.openInventory(gui);
    }

    public void saveInventory(Inventory inv) {
        System.arraycopy(inv.getContents(), 0, contents, 0, capacity);
    }
}
