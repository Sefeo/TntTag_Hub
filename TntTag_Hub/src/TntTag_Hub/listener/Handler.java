package TntTag_Hub.listener;

import TntTag_Hub.items.ServerJoinItem;
import TntTag_Hub.items.ShopItem;
import TntTag_Hub.manager.Manager;
import TntTag_Hub.scoreboard.Scoreboard;
import TntTag_Hub.shop.Buffs;
import TntTag_Hub.shop.Inventories;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class Handler implements Listener {

    private Inventory serverChooseInv;  // инвентарь выбора серверов
    private int serversUpdateTimer; // ежесекундный таймер обновления серверов

    private Manager manager;
    private BungeeListener bungeeListener;
    private ShopItem shopItem;
    private ServerJoinItem joinItem;
    private Buffs buffs;
    private Inventories inventories;

    public Handler(Manager manager) {
        this.manager = manager;
        this.buffs = manager.getBuffs();
        this.bungeeListener = manager.getBungeeListener();
        this.shopItem = manager.getShopItem();
        this.joinItem = manager.getJoinItem();
        this.inventories = manager.getInv();
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        manager.getCheckData().checkPlayerData(p); // проверка на то, есть ли игрок в БД

        e.setJoinMessage(null);
        p.setInvulnerable(true);
        p.setGameMode(GameMode.ADVENTURE);
        Location loc = manager.getConfig().loc;
        p.teleport(loc);
        p.setFoodLevel(100);
        p.setHealth(20F);
        p.setLevel(0);
        p.setExp(0F);

        p.getInventory().clear();
        shopItem.giveShop(p);

        Scoreboard.updateScoreboard(p);

        if(manager.getPlayers().size() == 1) // врубаем таймер только если зашел первый игрок
        {
            serversUpdateTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(manager.getPlugin(), () -> {
                bungeeListener.updateServersInfo(); // обновляем каждую секунду инфу о серверах

                for(Player pl: manager.getPlayers())
                {
                    if (p.getOpenInventory().getTopInventory().equals(serverChooseInv))
                        joinItem.createJoinItem(pl, serverChooseInv);
                }
            }, 20L, 20);
        }
    }

    @EventHandler
    private void onDisc(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        if(manager.getPlayers().size() == 1)
            Bukkit.getScheduler().cancelTask(serversUpdateTimer); // если вышел единственный игрок, прерываем обновление серверов
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if (item == null || item.getItemMeta() == null) return;

        e.setCancelled(true);

        if (item.getType().equals(Material.FEATHER) || item.getType().equals(Material.DIAMOND_SWORD) || item.getType().equals(Material.GOLDEN_APPLE))
            buffs.buyBuff(p, item.getType()); // покупаем баф в зависимости от нажатого предмета


        if(item.getType().equals(Material.TNT) && item.getItemMeta().getDisplayName() != null)
            bungeeListener.connectToServer(p, item.getItemMeta().getDisplayName()); // подключаем игрока к нажатому серверу
    }

    @EventHandler
    private void onInteractAtItem(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (item == null) return;

        if (item.getItemMeta().equals(shopItem.shopUse().getItemMeta())) inventories.createShopInv(p); // открываем окно магазина
        e.setCancelled(true);
    }


    @EventHandler
    private void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Player p = e .getPlayer();
        Entity ent = e.getRightClicked();

        if(!(e.getRightClicked() instanceof EntityArmorStand)) return;
        if(ent.getCustomName() == null) return;

        if(ent.getCustomName().equals(ChatColor.RED + "TNT TAG")) {
            serverChooseInv = Bukkit.createInventory(p, 9, "Сервера");
            p.openInventory(serverChooseInv); // открываем игроку инвентарь выбора сервера

            joinItem.createJoinItem(p, serverChooseInv);
        }
        e.setCancelled(true);
    }
}
