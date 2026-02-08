package net.paulem.vca.popups;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.paulem.vca.VCA;
import net.paulem.vca.villagers.VCAVillager;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Popup {
    public static Map<UUID, Popup> popups = new HashMap<>();

    @Getter
    private final VCAVillager vcaVillager;
    @Getter
    private final UUID displayUuid = UUID.randomUUID();
    @Getter
    private final UUID villagerId;
    @Getter
    private TextDisplay display;

    public Popup(@NotNull VCAVillager vcaVillager) {
        this.vcaVillager = vcaVillager;
        this.villagerId = this.vcaVillager.getUuid();
    }

    public void show() {
        VCA.getInstance().getScheduler().runTask(() -> {
            Villager villager = getVcaVillager().get();

            if(villager == null) {
                delete();
                return;
            }

            World world = villager.getWorld();
            display = world.spawn(villager.getLocation(), TextDisplay.class, textDisplay -> {
                textDisplay.text(Component.text("⚠").color(TextColor.color(0xFFC737)));
                textDisplay.setBillboard(Display.Billboard.CENTER);
                textDisplay.setInvulnerable(true);
                textDisplay.setSeeThrough(false);
                textDisplay.setDefaultBackground(false);
                textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);

                textDisplay.getPersistentDataContainer().set(VCA.getInstance().getPopupKey(), PersistentDataType.BOOLEAN, true);
            });

            villager.addPassenger(display);

            popups.put(getDisplayUuid(), this);
        });
    }

    public BoundingBox calcBoundingBox() {
        Villager villager = vcaVillager.get();
        if (villager == null || !villager.isValid()) return null;
        return BoundingBox.of(villager.getLocation().add(0, villager.getHeight(), 0), 0.5, 0.5, 0.5);
    }

    public void delete() {
        popups.remove(villagerId);
        if(getDisplay() != null) getDisplay().remove();
    }

    public void open(Player player) {
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(vcaVillager.getName())
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Hello, I'm a villager! What would you like to do?")),
                                DialogBody.plainMessage(Component.text("Mood : ").append(vcaVillager.getMood().getComponent()))
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.builder(Component.text("Talk"))
                                .width(80)
                                .build(),
                        ActionButton.builder(Component.text("Interact"))
                                .width(80)
                                .build(),
                        ActionButton.builder(Component.text("Trade"))
                                .width(80)
                                .build(),
                        ActionButton.builder(Component.text("Family Tree"))
                                .width(80)
                                .build()
                )).build())
        );
        player.showDialog(dialog);
    }
}
