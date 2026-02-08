package net.paulem.vca.villagers;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

public class NearVillagersManager {
    private final Map<Player, List<Villager>> villagers = new ConcurrentHashMap<>();

    public NearVillagersManager() { /* TODO document why this constructor is empty */ }

    public void clearVillagers(Player player) {
        this.villagers.remove(player);
    }

    public Set<Player> getPlayers() {
        return villagers.keySet();
    }

    public<T> List<T> forEach(BiFunction<Player, List<Villager>, T> function) {
        return villagers.entrySet().stream().map(entry -> function.apply(entry.getKey(), entry.getValue())).toList();
    }

    @Nullable
    public Villager pickVillager(Player player) {
        if(!villagers.containsKey(player)) return null;
        if(villagers.get(player).isEmpty()) return null;

        return villagers.get(player).get(ThreadLocalRandom.current().nextInt(0, villagers.get(player).size()));
    }

    public void updateVillagers(Player player, Villager... villagers) {
        updateVillagers(player, List.of(villagers));
    }

    public void updateVillagers(Player player, List<Villager> villagers) {
        this.villagers.put(player, villagers);
    }
}
