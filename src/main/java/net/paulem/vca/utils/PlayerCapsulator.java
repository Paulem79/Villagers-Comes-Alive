package net.paulem.vca.utils;

import org.bukkit.entity.Player;

public record PlayerCapsulator<T>(Player player, T data) {
}
