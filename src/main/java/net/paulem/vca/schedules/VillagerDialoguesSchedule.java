package net.paulem.vca.schedules;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.paulem.aihorde4j.dto.response.RequestStatusKobold;
import net.paulem.vca.VCA;
import net.paulem.vca.cache.CacheServices;
import net.paulem.vca.utils.Holders;
import net.paulem.vca.utils.HordeUtils;
import net.paulem.vca.villagers.NearVillagersManager;
import net.paulem.vca.villagers.VCAVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class VillagerDialoguesSchedule implements Runnable {
    @Override
    public void run() {
        NearVillagersManager nearVillagersManager = VCA.getInstance().getNearVillagersManager();

        // Pick a random nearby villager for each player
        for (Player player : nearVillagersManager.getPlayers()) {
            // 10% chance to continue
            if (ThreadLocalRandom.current().nextInt(100) > 10) continue;

            @Nullable VCAVillager vcaVillager = nearVillagersManager.pickVillager(player);
            if (vcaVillager == null) continue;

            String timeOfDay = player.getWorld().isDayTime() ? "day" : "night";

            CompletableFuture<Holders.Holder3<String, TextComponent, Component>> finishedSync = new CompletableFuture<>();
            CompletableFuture<Boolean> exitSync = new CompletableFuture<>();

            VCA.runSync(() -> {
                @Nullable Villager villager = vcaVillager.get();
                if (villager == null) {
                    exitSync.complete(true);
                    return;
                }

                String syncPlayerReputation = vcaVillager.getReputation(player).toString();
                TextComponent syncProfessionComponent = vcaVillager.getProfessionComponent();
                Component syncVillagerName = vcaVillager.getName() != null ? vcaVillager.getName() : syncProfessionComponent;

                finishedSync.complete(new Holders.Holder3<>(syncPlayerReputation, syncProfessionComponent, syncVillagerName));
            });

            exitSync.thenAccept(shouldExit -> {
                if (shouldExit) {
                    finishedSync.complete(null);
                }
            });

            finishedSync.thenAcceptAsync(result -> {
                if (result == null) return;
                String playerReputation = result.value1();
                TextComponent professionComponent = result.value2();
                Component villagerName = result.value3();

                List<String> models;
                try {
                    models = CompletableFuture.supplyAsync(() ->
                            CacheServices.HORDE_CACHE_SERVICE.get(VCA.getInstance().getClient())
                    ).orTimeout(10, java.util.concurrent.TimeUnit.SECONDS).join();
                } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                }

                CompletableFuture<RequestStatusKobold> textGenerationFuture = VCA.getInstance().getClient().generateTextAsync(
                        HordeUtils.getTextPrompt(professionComponent.content(), timeOfDay, playerReputation),
                        HordeUtils.getTextParams(),
                        models,
                        (waitTime, queuePosition, processing, finished) -> VCA.getInstance().getLogger().info("Text generation progress - Wait time: " + waitTime + "s, Queue position: " + queuePosition + ", Processing: " + processing + ", Finished: " + finished)
                );

                textGenerationFuture.thenAccept(response -> {
                    if (response.getGenerations().isEmpty()) return;

                    // Format to JSON
                    String text = response.getGenerations().getFirst().getText().trim();

                    String jsonText = HordeUtils.extractJson(text);

                    try {
                        JsonElement jsonResponse = new Gson().fromJson(jsonText, JsonElement.class);

                        if (jsonResponse == null || !jsonResponse.isJsonObject()) {
                            return;
                        }

                        String message = jsonResponse.getAsJsonObject().has("dialogue")
                                ? jsonResponse.getAsJsonObject().get("dialogue").getAsString()
                                : "";

                        if (message.isEmpty()) return;

                        player.sendRichMessage("<villagername> says: <message>",
                                Placeholder.component("villagername", villagerName),
                                Placeholder.component("message", Component.text(message))
                        );
                    } catch (Exception ignored) {
                        // Ignore parsing errors
                    }
                });
            });
        }
    }
}
