package net.paulem.vca.listeners;

import net.paulem.vca.VCA;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerListeners implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.hasChangedBlock()) return;

        updateVillagersList(event);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateVillagersList(event);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        VCA.getInstance().getNearVillagersManager().clearVillagers(event.getPlayer());
    }

    private static void updateVillagersList(PlayerEvent event) {
        Player player = event.getPlayer();
        Villager[] villagers = player.getNearbyEntities(10, 10, 10)
                .stream()
                .filter(Villager.class::isInstance)
                .map(Villager.class::cast)
                .toArray(Villager[]::new);

        VCA.getInstance().getNearVillagersManager().updateVillagers(player, villagers);
    }
}
