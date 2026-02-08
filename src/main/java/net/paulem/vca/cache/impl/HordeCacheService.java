package net.paulem.vca.cache.impl;

import com.github.benmanes.caffeine.cache.*;
import net.paulem.aihorde4j.client.HordeClient;
import net.paulem.aihorde4j.dto.response.ActiveModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HordeCacheService extends CacheService<HordeClient, List<String>> {
    public HordeCacheService() {
        super(5, TimeUnit.HOURS, HordeCacheService::chooseModels);
    }

    private static List<String> chooseModels(HordeClient client) {
        List<ActiveModel> text = client.getActiveModels("text");

        if (text == null) {
            return List.of();
        }

        List<String> nonThinkingModels = text.stream()
                .map(ActiveModel::getName)
                .filter(name -> !name.toLowerCase().contains("thinking"))
                .toList();

        List<String> instructModels = nonThinkingModels.stream()
                .filter(name -> name.toLowerCase().contains("instruct"))
                .toList();

        if(instructModels.isEmpty()) {
            if(nonThinkingModels.isEmpty()) {
                return text
                        .stream()
                        .map(ActiveModel::getName)
                        .toList();
            }

            return nonThinkingModels;
        }

        return instructModels;
    }
}