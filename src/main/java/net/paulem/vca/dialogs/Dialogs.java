package net.paulem.vca.dialogs;

import io.papermc.paper.dialog.Dialog;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.paulem.vca.VCA;
import net.paulem.vca.codecs.UuidCodec;
import net.paulem.vca.dialogs.categories.MainMenuCategories;
import net.paulem.vca.utils.VillagersUtils;
import net.paulem.vca.villagers.VCAVillager;

import java.util.List;
import java.util.function.Function;

/**
 * A utility class for defining and managing reusable dialogs.
 *<br>
 * This class is final and cannot be instantiated. It provides static constants for creating dialogs that can be used across the application.
 */
public final class Dialogs {
    private Dialogs() {}

    public static final Function<VCAVillager, Dialog> MAIN_VILLAGER_DIALOG = vcaVillager -> {
        DynamicDialog<VCAVillager, VCAVillager> dynamicDialog = new DynamicDialog<>(VCAVillager::getName, true);

        List<DynamicButton<VCAVillager>> buttons = List.of(
                new DynamicButton<>(dialogData -> true, null, Component.text("Talk"), 80),

                new DynamicButton<>(dialogData -> true, null, Component.text("Interact"), 80),

                new DynamicButton<>(dialogData -> true, null, Component.text("Family Tree"), 80),

                new DynamicButton<>(
                        dialogData -> dialogData.get() != null && VillagersUtils.canProfessionTrade(dialogData.get()),
                        new DynamicDialogAction<>(
                                VCA.key("trade"),
                                MainMenuCategories::trade,
                                binaryTagData -> BinaryTagHolder.binaryTagHolder(UuidCodec.INSTANCE.encode(binaryTagData.getUuid()))
                        ),
                        Component.text("Trade"), 80)
        );

        return dynamicDialog.create(vcaVillager, vcaVillager, List.of(
                Component.text("Profession : ").append(vcaVillager.getProfessionComponent()),
                Component.text("Personality : ").append(vcaVillager.getPersonality().getComponent()),
                Component.text("Mood : ").append(vcaVillager.getMood().getComponent())
        ), buttons);
    };
}
