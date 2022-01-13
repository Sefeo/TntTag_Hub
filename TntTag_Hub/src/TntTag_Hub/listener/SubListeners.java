package TntTag_Hub.listener;

import TntTag_Hub.manager.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemMendEvent;

public class SubListeners implements Listener {
    private Manager manager;

    public SubListeners(Manager manager) {
        this.manager = manager;
    }

    @EventHandler
    private void onItemDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onDamageTaken(EntityDamageEvent e) {
        if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) e.setDamage(0F);
        else e.setCancelled(true);
    }

    @EventHandler
    private void onMobSpawn(CreatureSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onItemMend(PlayerItemMendEvent e) { // я не уверен тот ли это ивент, что мне нужен, но лишним не будет
        e.setCancelled(true);
    }
}
