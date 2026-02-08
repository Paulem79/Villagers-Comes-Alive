package net.paulem.vca.listeners;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.paulem.vca.VCA;
import net.paulem.vca.codecs.UuidCodec;
import net.paulem.vca.utils.VillagersUtils;
import net.paulem.vca.villagers.LoadedVillagersManager;
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
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.MerchantInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InteractListeners implements Listener {
    // Very short lifetime
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

        BinaryTagHolder binaryTagHolder = BinaryTagHolder.binaryTagHolder(UuidCodec.INSTANCE.encode(vcaVillager.getUuid()));

        List<ActionButton> buttons = new ArrayList<>(List.of(
                ActionButton.builder(Component.text("Talk"))
                        .width(80)
                        .build(),
                ActionButton.builder(Component.text("Interact"))
                        .width(80)
                        .build(),
                ActionButton.builder(Component.text("Family Tree"))
                        .width(80)
                        .build()
        ));

        if(VillagersUtils.canProfessionTrade(villager)) {
            buttons.add(2,
                    ActionButton.builder(Component.text("Trade"))
                            .width(80)
                            .action(DialogAction.customClick(VCA.key("trade"), binaryTagHolder))
                            .build());
        }

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(vcaVillager.getName())
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Hello, I'm a villager! What would you like to do?")),
                                DialogBody.plainMessage(Component.text("Profession : ").append(vcaVillager.getProfessionComponent())),
                                DialogBody.plainMessage(Component.text("Personality : ").append(vcaVillager.getPersonality().getComponent())),
                                DialogBody.plainMessage(Component.text("Mood : ").append(vcaVillager.getMood().getComponent()))
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.multiAction(buttons).build())
        );
        player.showDialog(dialog);
    }

    @EventHandler
    void handleLevelsDialog(PlayerCustomClickEvent event) {
        if (!event.getIdentifier().equals(VCA.key("trade"))) {
            return;
        }

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
            ignoreDialogOpen.add(player);

            VCA.getInstance().getScheduler().runTaskLater(() -> {
                player.openInventory(MenuType.MERCHANT.builder()
                        .merchant(villager)
                        .checkReachable(true)
                        .build(player));
            }, 1L);
        }
    }
}
