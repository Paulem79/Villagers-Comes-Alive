package net.paulem.vca.villagers;

import lombok.Getter;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LoadedVillagersManager {
    @Getter
    private static final LoadedVillagersManager instance = new LoadedVillagersManager();

    @Getter
    private final Map<UUID, VCAVillager> loadedVillagers = new ConcurrentHashMap<>();

    @Nullable
    public VCAVillager get(@NotNull UUID uuid) {
        return get(uuid, null);
    }

    @Nullable
    public VCAVillager get(@NotNull Villager villager) {
        return get(villager.getUniqueId(), villager);
    }

    @Nullable
    public VCAVillager get(@NotNull UUID uuid, @Nullable Villager villager) {
        VCAVillager vcaVillager = loadedVillagers.get(uuid);
        return vcaVillager != null ? vcaVillager : add(uuid, villager);
    }

    @Nullable
    public VCAVillager add(@NotNull UUID uuid, @Nullable Villager villager) {
        if(loadedVillagers.containsKey(uuid)) return loadedVillagers.get(uuid);
        if(villager == null) return null;

        return add(uuid, new VCAVillager(villager));
    }

    @Nullable
    public VCAVillager add(@NotNull UUID uuid, @NotNull VCAVillager vcaVillager) {
        if(loadedVillagers.containsKey(uuid)) return loadedVillagers.get(uuid);

        loadedVillagers.put(uuid, vcaVillager);
        return vcaVillager;
    }
}
