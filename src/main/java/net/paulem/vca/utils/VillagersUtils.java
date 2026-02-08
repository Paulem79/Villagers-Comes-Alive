package net.paulem.vca.utils;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.EnumMap;
import java.util.Map;

public class VillagersUtils {
    private VillagersUtils() {}

    public static ReputationType getAverageReputation(Player player, Villager villager) {
        Reputation reputation = villager.getReputation(player.getUniqueId());

        Map<ReputationType, Integer> reputations = new EnumMap<>(ReputationType.class);
        for (ReputationType type : ReputationType.values()) {
            int reputationScore = reputation.getReputation(type);
            reputations.put(type, reputationScore);
        }

        // Get the highest reputation towards the player, or TRADING if all are at same
        Integer maxReputation = reputations.values()
                .stream()
                .max(Integer::compareTo)
                .orElse(0);
        if(maxReputation == 0) {
            return ReputationType.TRADING;
        }

        return reputations.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(maxReputation))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(ReputationType.TRADING);
    }
}
