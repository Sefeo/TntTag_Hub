package TntTag_Hub;

import TntTag_Hub.listener.BungeeListener;
import TntTag_Hub.listener.Handler;
import TntTag_Hub.listener.SubListeners;
import TntTag_Hub.manager.Manager;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Main extends JavaPlugin {

    private Manager manager;
    private static Main instance;
    private Connection connection;

    private ArmorStand npc;

    @Override
    public void onEnable() {
        manager = new Manager(this);

        saveDefaultConfig();
        manager.getConfig().loadConfig();

        setInstance(this);
        listConnection();

        System.out.println("[TntTag_Hub] Loaded!");

        Bukkit.getPluginManager().registerEvents(new Handler(manager), this);
        Bukkit.getPluginManager().registerEvents(new SubListeners(manager), this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener(manager));

        Location loc = manager.getConfig().npcLoc;
        npc = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        npc.setHelmet(new ItemStack(Material.TNT));
        npc.setCustomName(ChatColor.RED + "TNT TAG");
        npc.setCustomNameVisible(true);

        int removedEntities = 0;
        for(Entity ent : loc.getWorld().getEntities()) {
            if(ent instanceof Player || ent instanceof ArmorStand) continue; // удаляем всех энтити кроме игроков и арморстендов
            ent.remove();
            removedEntities++;
        }
        if(removedEntities > 0) System.out.println("[TntTag_Hub] Entities deleted: " + removedEntities);
    }

    public void onDisable() {
        npc.remove();
    }

    public static Main getInstance() {
        return instance;
    }

    private static void setInstance(Main instance) {
        Main.instance = instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void listConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectUrl = "jdbc:mysql://" + manager.getConfig().dbUrl + "/" + manager.getConfig().dbMainTable;
            this.connection = DriverManager.getConnection(connectUrl, manager.getConfig().dbUser, manager.getConfig().dbPassword);

            String createTable = "CREATE TABLE IF NOT EXISTS " + manager.getConfig().dbMainTable + "." + manager.getConfig().dbTable + " (" // подключаемся к бд и создаем таблицу
                    + "ID INT(11) NOT NULL primary key AUTO_INCREMENT,"
                    + "UUID VARCHAR(36) NOT NULL,"
                    + "Nick VARCHAR(26) NOT NULL,"
                    + "Money INT(11) NOT NULL,"
                    + "Win INT(11) NOT NULL,"
                    + "Game INT(11) NOT NULL,"
                    + "Speed INT(11) NOT NULL,"
                    + "Slow INT(11) NOT NULL,"
                    + "Invul INT(11) NOT NULL)";
            Statement statement = this.connection.createStatement();
            statement.execute(createTable);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[TntTag_Hub] Couldn`t connect to the database, disabling plugin...");
            onDisable();
        }
    }
}
