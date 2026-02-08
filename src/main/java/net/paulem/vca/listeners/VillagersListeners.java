package net.paulem.vca.listeners;

import net.paulem.vca.villagers.LoadedVillagersManager;
import net.paulem.vca.villagers.VCAVillager;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.EntitiesLoadEvent;

public class VillagersListeners implements Listener {
    @EventHandler
    public void onLoad(EntitiesLoadEvent event) {
        event.getEntities().stream().filter(Villager.class::isInstance).forEach(entity -> {
            Villager villager = (Villager) entity;
            VCAVillager.of(villager);
        });
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if(event.getEntity() instanceof Villager villager) {
            VCAVillager.of(villager);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof Villager villager) {
            LoadedVillagersManager.getInstance().getLoadedVillagers().remove(villager.getUniqueId());
        }
    }
}
