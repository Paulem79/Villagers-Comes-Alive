package net.paulem.vca.villagers;

import com.destroystokyo.paper.entity.villager.ReputationType;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.paulem.vca.VCA;
import net.paulem.vca.utils.VillagersUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VCAVillager {

    @Getter
    private final UUID uuid;
    // TODO: Update world when the villager is loaded in a different world
    @Getter
    @Setter
    private UUID worldUuid;

    @Getter
    private Mood mood;
    @Getter
    private Personality personality;

    public static VCAVillager of(Villager villager) {
        @Nullable VCAVillager cachedVillager = LoadedVillagersManager.getInstance().get(villager);
        return cachedVillager != null ? cachedVillager : new VCAVillager(villager);
    }

    public VCAVillager(Villager villager) {
        this.uuid = villager.getUniqueId();
        this.worldUuid = villager.getWorld().getUID();

        this.mood = Mood.NEUTRAL;
        this.personality = Personality.NORMAL;
    }

    @Nullable
    public Component getName() {
        return Optional.ofNullable(get())
                .map(Villager::customName)
                .orElse(null);
    }

    @Nullable
    public Villager get() {
        World world = VCA.getInstance().getServer().getWorld(worldUuid);

        Entity foundEntity = world.getEntity(uuid);
        if (foundEntity == null) return null;

        return foundEntity instanceof Villager villager ? villager : null;
    }

    public ReputationType getReputation(Player player) {
        Villager villager = get();
        if(villager == null) throw new IllegalStateException("Villager is not loaded");

        return VillagersUtils.getAverageReputation(player, villager);
    }

    public Component getReputationComponent(Player player) {
        ReputationType reputation = getReputation(player);
        Component color = switch (reputation) {
            case MAJOR_NEGATIVE -> Component.empty().color(TextColor.color(0xFF0000));
            case MINOR_NEGATIVE -> Component.empty().color(TextColor.color(0xFFA500));
            case MINOR_POSITIVE -> Component.empty().color(TextColor.color(0x00FFFF));
            case MAJOR_POSITIVE -> Component.empty().color(TextColor.color(0x00FF00));
            case TRADING -> Component.empty().color(TextColor.color(0xFFFFFF));
        };

        String reputationLabel = StringUtils.capitalize(
                reputation.name().replace("_", " ").toLowerCase()
        );
        return Component.text(reputationLabel).color(color.color());
    }

    public Villager.Profession getProfession() {
        return get().getProfession();
    }

    public TextComponent getProfessionComponent() {
        String professionName = getProfession().equals(Villager.Profession.NONE) ? "Unemployed" : StringUtils.capitalize(getProfession().getKey().getKey());
        return Component.text(professionName).color(TextColor.color(0xFFFFFF));
    }

    public enum Mood {
        HAPPY(Component.text("Happy").color(TextColor.color(0x00FF00))),
        NEUTRAL(Component.text("Neutral").color(TextColor.color(0xFFFF00))),
        UNHAPPY(Component.text("Unhappy").color(TextColor.color(0xFFA500))),
        ANGRY(Component.text("Angry").color(TextColor.color(0xFF0000)));

        @Getter
        private final Component component;

        Mood(Component component) {
            this.component = component;
        }
    }

    public enum Personality {
        NORMAL(Component.text("Normal").color(TextColor.color(0xFFFFFF))),
        WEAK(Component.text("Weak").color(TextColor.color(0x00FFFF)));

        @Getter
        private final Component component;

        Personality(Component component) {
            this.component = component;
        }
    }
}
