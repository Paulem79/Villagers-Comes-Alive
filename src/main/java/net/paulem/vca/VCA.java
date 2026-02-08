package net.paulem.vca;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.paulem.aihorde4j.client.HordeClient;
import net.paulem.vca.listeners.InteractListeners;
import net.paulem.vca.listeners.VillagersListeners;
import net.paulem.vca.schedules.VillagerDialoguesSchedule;
import net.paulem.vca.listeners.PlayerListeners;
import net.paulem.vca.villagers.NearVillagersManager;
import net.paulem.vca.villagers.VCAVillager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class VCA extends JavaPlugin {
    @Getter
    private static VCA instance;

    @Getter
    private TaskScheduler scheduler;
    @Getter
    private final NamespacedKey popupKey = new NamespacedKey(this, "popup");

    @Getter
    private final HordeClient client = HordeClient.createDefault();

    @Getter
    private final NearVillagersManager nearVillagersManager = new NearVillagersManager();

    @Override
    public void onEnable() {
        super.onEnable();

        instance = this;

        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        getServer().getPluginManager().registerEvents(new InteractListeners(), this);
        getServer().getPluginManager().registerEvents(new VillagersListeners(), this);

        scheduler = UniversalScheduler.getScheduler(this);

        scheduler.runTaskTimerAsynchronously(new VillagerDialoguesSchedule(), 20L * 5, 20L * 15);

        scheduler.runTask(() -> {
            for (World world : Bukkit.getWorlds()) {
                for (TextDisplay textDisplay : world.getEntitiesByClass(TextDisplay.class)) {
                    if (textDisplay.getPersistentDataContainer().has(popupKey, PersistentDataType.BOOLEAN)) {
                        textDisplay.remove();
                    }
                }

                for (Villager villager : world.getEntitiesByClass(Villager.class)) {
                    VCAVillager.of(villager);
                }
            }
        });

        getLogger().info("VCA enabled!");
    }

    @Override
    public void onDisable() {
        super.onDisable();

        getLogger().info("VCA disabled!");
    }

    public static Key key(String name) {
        return Key.key("vca", name);
    }
}
