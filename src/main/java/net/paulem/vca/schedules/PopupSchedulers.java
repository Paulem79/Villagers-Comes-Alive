package net.paulem.vca.schedules;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import net.paulem.vca.popups.Popup;
import net.paulem.vca.utils.PlayerUtils;
import net.paulem.vca.villagers.LoadedVillagersManager;
import net.paulem.vca.villagers.VCAVillager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;

public class PopupSchedulers {
    private PopupSchedulers() {}

    public static class PlayerCheck implements Runnable {
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                int maxReach = PlayerUtils.getReach(player);

                @Nullable RayTraceResult hitResult = player.rayTraceEntities(maxReach);
                if(hitResult == null) continue;

                @Nullable Entity entity = hitResult.getHitEntity();
                if(entity == null) continue;

                if(entity instanceof Villager villager) {
                    VCAVillager vcaVillager = LoadedVillagersManager.getInstance().get(villager);
                    if(vcaVillager == null) continue;

                    if(Popup.popups.values().stream().anyMatch(popup -> popup.getVillagerId().equals(vcaVillager.getUuid())))
                        continue;

                    new Popup(vcaVillager).show();
                }
            }
        }
    }
}
