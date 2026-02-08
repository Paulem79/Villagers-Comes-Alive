package net.paulem.vca.schedules;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.paulem.aihorde4j.dto.response.RequestStatusKobold;
import net.paulem.vca.VCA;
import net.paulem.vca.utils.HordeUtils;
import net.paulem.vca.utils.VillagersUtils;
import net.paulem.vca.villagers.NearVillagersManager;
import org.apache.commons.lang3.StringUtils;
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
        // TODO: Merge with VCAVillager
        for (Player player : nearVillagersManager.getPlayers()) {
            // 10% chance to continue
            if (ThreadLocalRandom.current().nextInt(100) > 10) continue;

            @Nullable Villager villager = nearVillagersManager.pickVillager(player);
            if (villager == null) continue;

            String timeOfDay = player.getWorld().isDayTime() ? "day" : "night";

            String playerReputation = VillagersUtils.getAverageReputation(player, villager).toString();

            // TODO: Merge with VCAVillager#getProfessionComponent
            Villager.Profession profession = villager.getProfession();

            String professionName = StringUtils.capitalize(profession.getKey().getKey());
            if (profession.equals(Villager.Profession.NONE)) {
                professionName = "Unemployed";
            }

            String villagerName = villager.customName() != null ? villager.customName().toString() : professionName;

            List<String> models = HordeUtils.chooseModels(VCA.getInstance().getClient());

            CompletableFuture<RequestStatusKobold> textGenerationFuture = VCA.getInstance().getClient().generateTextAsync(
                    HordeUtils.getTextPrompt(professionName, timeOfDay, playerReputation),
                    HordeUtils.getTextParams(),
                    models,
                    null);

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

                    System.out.println(villagerName);

                    player.sendRichMessage("<villagername> says: <message>",
                            Placeholder.component("villagername", Component.text(villagerName)),
                            Placeholder.component("message", Component.text(message))
                    );
                } catch (Exception ignored) {
                    // Ignore parsing errors
                }
            });
        }
    }
}
