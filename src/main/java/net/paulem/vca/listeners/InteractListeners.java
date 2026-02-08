package net.paulem.vca.listeners;

import net.paulem.vca.popups.Popup;
import net.paulem.vca.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

public class InteractListeners implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if(!(event.getRightClicked() instanceof TextDisplay textDisplay)) return;

        if(!Popup.popups.containsKey(textDisplay.getUniqueId())) return;
        Popup popup = Popup.popups.get(textDisplay.getUniqueId());

        event.setCancelled(true);

        popup.open(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;

        Player player = event.getPlayer();
        for (Popup popup : Popup.popups.values()) {
            BoundingBox boundingBox = popup.calcBoundingBox();
            if(boundingBox == null) continue;

            int maxReach = PlayerUtils.getReach(player);
            RayTraceResult result = boundingBox.rayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection(), maxReach);

            if(result != null) {
                popup.open(player);
                break;
            }
        }
    }
}
