package TntTag_Hub.items;

import TntTag_Hub.manager.Manager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerJoinItem {

    private Manager manager;

    public ServerJoinItem(Manager manager) {
        this.manager = manager;
    }

    // эти переменные должны быть именно тут, хотел засунуть их в BungeeListener, но тогда они не успевают обрабатываться и флудят ексепшинами
    public String[] serverName;
    public HashMap<String, Boolean> roundStarted = new HashMap<>(); // все сервера и то, идёт ли на них раунд
    public HashMap<String, Integer> playersCount = new HashMap<>(); // все сервера и их кол-во игроков
    public HashMap<String, Boolean> isOnline = new HashMap<>(); // все сервера и их статус (онлайн/офлайн)

    public void createJoinItem(Player p, Inventory inv) { // создание иконок серверов в инвентаре
        int count = 0; // слот сервера в инвентаре

        for (String server : serverName) { // проходимся по каждому серверу и добавляем его

            if (server.equals("HUB")) continue;
            if (!isOnline.get(server)) continue;
            if (playersCount.get(server) == 24) continue;
            if (roundStarted.get(server)) continue;
            if(count > 9) {
                System.out.println("[ERROR] Алло, ты там что, вообще ебобо? Инвентарь на 9 слотов, а у тебя больше 9-и серверов. Иди переписывать его или офай лишние");
                p.closeInventory();
                p.sendMessage(ChatColor.RED + "Произошла ошибка, обратитесь к администрации");
                return;
            }

            ItemStack serverItem = new ItemStack(Material.TNT);
            ItemMeta serverMeta = serverItem.getItemMeta();
            serverMeta.setDisplayName(server);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RESET + "Игроков: " + playersCount.get(server) + "/24");
            serverMeta.setLore(lore);
            serverItem.setItemMeta(serverMeta);

            inv.setItem(count, serverItem);
            count++;
        }
    }
}
