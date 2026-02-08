package net.paulem.vca.utils;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import org.bukkit.entity.Player;

public class PlayerUtils {
    public static int getReach(Player player) {
        AttackRange playerData = player.getDataOrDefault(DataComponentTypes.ATTACK_RANGE, AttackRange.attackRange().maxReach(5).build());
        return (int) playerData.maxReach();
    }
}
