package TntTag_Hub.shop;

import TntTag_Hub.Main;
import TntTag_Hub.data.UpdateData;
import TntTag_Hub.manager.Manager;
import TntTag_Hub.scoreboard.Scoreboard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Buffs {

    private Manager manager;
    private UpdateData updateData;

    private int price;
    private int speedValue;
    private int slowValue;
    private int invulValue;

    public Buffs(Manager manager) {
        this.manager = manager;
        this.updateData = manager.getUpdateData();
    }

    public void buyBuff(Player p, Material material) {

        speedValue = getBuffValue(p, "Speed");
        slowValue = getBuffValue(p, "Slow");
        invulValue = getBuffValue(p, "Invul");
        int money = getMoney(p);
        int cost;

        switch (String.valueOf(material))
        {
            case "FEATHER": {
                if (speedValue == 5) return;

                cost = getItemPrice(p, "Speed");
                if (money < cost) {
                    p.sendMessage(ChatColor.YELLOW + "Недостаточно монет!");
                    return;
                }
                updateData.updatePlayerMoney(p, cost, false);
                updateData.addBuffCount(p, "Speed");
                p.sendMessage(ChatColor.GREEN + "Покупка успешна!");
                break;
            }
            case "DIAMOND_SWORD": {
                if (slowValue == 5) return;

                cost = getItemPrice(p, "Slow");
                if (money < cost) {
                    p.sendMessage(ChatColor.YELLOW + "Недостаточно монет!");
                    return;
                }
                updateData.updatePlayerMoney(p, cost, false);
                updateData.addBuffCount(p, "Slow");
                p.sendMessage(ChatColor.GREEN + "Покупка успешна!");
                break;
            }
            case "GOLDEN_APPLE": {
                if (invulValue == 5) return;

                cost = getItemPrice(p, "Invul");
                if (money < cost) {
                    p.sendMessage(ChatColor.YELLOW + "Недостаточно монет!");
                    return;
                }
                updateData.updatePlayerMoney(p, cost, false);
                updateData.addBuffCount(p, "Invul");
                p.sendMessage(ChatColor.GREEN + "Покупка успешна!");
                break;
            }
        }

        Scoreboard.updateScoreboard(p); // в скорборде обновляем деньги
        manager.getInv().createShopInv(p);
    }

    int getItemPrice(Player p, String item) {

        speedValue = getBuffValue(p, "Speed");
        slowValue = getBuffValue(p, "Slow");
        invulValue = getBuffValue(p, "Invul");

        switch(item) {
            case "Speed": {
                if(speedValue >= 5) price = manager.getConfig().invulBuffCost.get(speedValue);
                else price = manager.getConfig().speedBuffCost.get(speedValue+1);
                break;
            }
            case "Slow": {
                if(slowValue >= 5) price = manager.getConfig().invulBuffCost.get(slowValue);
                else price = manager.getConfig().slowBuffCost.get(slowValue+1);
                break;
            }
            case "Invul": {
                if(invulValue >= 5) price = manager.getConfig().invulBuffCost.get(invulValue);
                else price = manager.getConfig().invulBuffCost.get(invulValue+1);
                break;
            }
        }
        return price;
    }

    int getBuffValue(Player p, String type) {
        int buffValue = 5; // 5 по умолчанию, чтобы в случае ошибки оно считало, что у игрока максимальный уровень бафа и не давало купить новые
        try {
            PreparedStatement statement = Main.getInstance().getConnection().prepareStatement(
                    "SELECT * FROM users WHERE UUID = ?"
            );
            statement.setString(1, p.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            result.next();
            buffValue = result.getInt(type);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffValue;
    }

    private int getMoney(Player p) {
        int money = 0;
        try {
            PreparedStatement statement = Main.getInstance().getConnection().prepareStatement(
                    "SELECT Money FROM users WHERE `UUID` = ?"
            );
            statement.setString(1, p.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            result.next();
            money = result.getInt("Money");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return money;
    }
}
