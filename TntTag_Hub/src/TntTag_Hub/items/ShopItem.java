package TntTag_Hub.items;

import TntTag_Hub.manager.Manager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopItem {

    private Manager manager;

    public ShopItem(Manager manager) {
        this.manager = manager;
    }

    public void giveShop(Player p) { p.getInventory().setItem(4, shopUse()); }

    public ItemStack shopUse() {
        ItemStack shop = new ItemStack(Material.EMERALD);
        ItemMeta meta = shop.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Магазин");
        shop.setItemMeta(meta);
        return shop;
    }
}
