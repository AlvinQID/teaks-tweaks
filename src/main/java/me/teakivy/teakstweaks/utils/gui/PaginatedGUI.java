package me.teakivy.teakstweaks.utils.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PaginatedGUI {
    private final static HashMap<UUID, PaginatedGUI> guis = new HashMap<>();
    private int page = 0;
    private final List<ItemStack> items;
    private final Inventory inv;

    public PaginatedGUI(List<ItemStack> items, String title) {
        this.items = items;
        this.inv = Bukkit.createInventory(null, 54, title);
    }

    public void open(Player player) {
        player.openInventory(inv);
        update();

        guis.put(player.getUniqueId(), this);
    }

    public void nextPage() {
        if (page < items.size() / 45) {
            page++;
            update();
        }
    }

    public void previousPage() {
        if (page > 0) {
            page--;
            update();
        }
    }

    private void update() {
        inv.clear();
        for (int i = 0; i < 45; i++) {
            int index = i + (page * 45);
            if (index >= items.size()) break;
            inv.setItem(i, items.get(index));
        }

        // Add navigation buttons
        ItemStack nextButton = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextButton.getItemMeta();
        nextMeta.setDisplayName(ChatColor.GOLD + "Next Page");
        nextButton.setItemMeta(nextMeta);

        ItemStack prevButton = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prevButton.getItemMeta();
        prevMeta.setDisplayName(ChatColor.GOLD + "Previous Page");
        prevButton.setItemMeta(prevMeta);

        if (page < items.size() / 45) inv.setItem(53, nextButton);
        if (page > 0) inv.setItem(45, prevButton);
    }

    public static HashMap<UUID, PaginatedGUI> getGuis() {
        return guis;
    }

    public static PaginatedGUI getGui(Player player) {
        return guis.get(player.getUniqueId());
    }

    public static void removeGui(Player player) {
        guis.remove(player.getUniqueId());
    }

    public static void next(Player player) {
        PaginatedGUI gui = getGui(player);
        gui.nextPage();
    }

    public static void previous(Player player) {
        PaginatedGUI gui = getGui(player);
        gui.previousPage();
    }
}
