package net.paulem.vca.listeners;

import io.papermc.paper.event.player.PlayerCustomClickEvent;
import lombok.Getter;
import net.paulem.vca.dialogs.Dialogs;
import net.paulem.vca.dialogs.DynamicDialogAction;
import net.paulem.vca.utils.PlayerCapsulator;
import net.paulem.vca.utils.VillagersUtils;
import net.paulem.vca.villagers.VCAVillager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.MerchantInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// Remove experimental warning
public class InteractListeners implements Listener {
    // Very short lifetime
    @Getter
    private static List<Player> ignoreDialogOpen = new ArrayList<>();

    @EventHandler
    public void onJoblessVillagerInteract(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Villager villager)) return;

        if(VillagersUtils.canProfessionTrade(villager)) return;

        openDialog(event, event.getPlayer(), villager);
    }

    @EventHandler
    public void onMerchantInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if(!(inventory instanceof MerchantInventory merchantInventory)) return;

        HumanEntity humanEntity = event.getPlayer();
        if(!(humanEntity instanceof Player player)) return;

        if(!(merchantInventory.getMerchant() instanceof Villager villager)) return;

        openDialog(event, player, villager);
    }

    private static void openDialog(Cancellable cancellable, Player player, Villager villager) {
        if(ignoreDialogOpen.contains(player)) {
            ignoreDialogOpen.remove(player);
            return;
        }

        cancellable.setCancelled(true);

        VCAVillager vcaVillager = VCAVillager.of(villager);

        player.showDialog(Dialogs.MAIN_VILLAGER_DIALOG.apply(new PlayerCapsulator<>(player, vcaVillager)));
    }

    @EventHandler
    public void handleDynamicDialog(PlayerCustomClickEvent event) {
        @Nullable DynamicDialogAction<?> dialogAction = DynamicDialogAction.REGISTRY.get(event.getIdentifier());

        if(dialogAction == null) return;
        dialogAction.execute(event);
    }
}
