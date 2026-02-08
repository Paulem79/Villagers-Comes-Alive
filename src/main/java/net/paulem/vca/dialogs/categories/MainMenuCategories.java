package net.paulem.vca.dialogs.categories;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.paulem.vca.VCA;
import net.paulem.vca.codecs.UuidCodec;
import net.paulem.vca.listeners.InteractListeners;
import net.paulem.vca.villagers.LoadedVillagersManager;
import net.paulem.vca.villagers.VCAVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class MainMenuCategories {
    private MainMenuCategories() {}

    public static void trade(PlayerCustomClickEvent event) {
        DialogResponseView view = event.getDialogResponseView();
        if (view == null) {
            return;
        }

        BinaryTagHolder binaryTagHolder = event.getTag();
        String stringUuid = binaryTagHolder.string();
        UUID villagerUuid = UuidCodec.INSTANCE.decode(stringUuid);

        if (event.getCommonConnection() instanceof PlayerGameConnection connection) {
            @Nullable VCAVillager vcaVillager = LoadedVillagersManager.getInstance().get(villagerUuid);

            if(vcaVillager == null) return;

            @Nullable Villager villager = vcaVillager.get();
            if(villager == null) return;

            Player player = connection.getPlayer();
            player.closeDialog();
            InteractListeners.getIgnoreDialogOpen().add(player);

            VCA.getInstance().getScheduler().runTaskLater(() -> {
                player.openInventory(MenuType.MERCHANT.builder()
                        .merchant(villager)
                        .checkReachable(true)
                        .build(player));
            }, 1L);
        }
    }
}
