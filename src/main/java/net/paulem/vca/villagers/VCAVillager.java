package net.paulem.vca.villagers;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.paulem.vca.VCA;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VCAVillager {

    @Getter
    private final UUID uuid;
    // TODO: Update world when the villager is loaded in a different world
    @Getter
    @Setter
    private UUID worldUuid;

    @Getter
    private Villager.Profession profession;
    @Getter
    private Mood mood;
    @Getter
    private Personality weak;

    public static VCAVillager of(Villager villager) {
        @Nullable VCAVillager cachedVillager = LoadedVillagersManager.getInstance().get(villager);
        return cachedVillager != null ? cachedVillager : new VCAVillager(villager);
    }

    public VCAVillager(Villager villager) {
        this.uuid = villager.getUniqueId();
        this.worldUuid = villager.getWorld().getUID();

        this.profession = villager.getProfession();
        this.mood = Mood.NEUTRAL;
        this.weak = Personality.NORMAL;
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
        NORMAL,
        WEAK;
    }
}
