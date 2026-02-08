package net.paulem.vca.utils;

import net.paulem.aihorde4j.client.HordeClient;
import net.paulem.aihorde4j.dto.params.ModelGenerationInputKobold;
import net.paulem.aihorde4j.dto.response.ActiveModel;

import java.util.List;

public class HordeUtils {
    private HordeUtils() {
    }

    public static List<String> chooseModels(HordeClient client) {
        List<ActiveModel> text = client.getActiveModels("text");

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

    public static ModelGenerationInputKobold getTextParams() {
        return new ModelGenerationInputKobold()
                .setMaxLength(500)
                .setTemperature(0.7f)
                .setTopP(0.9f)
                .setRepPen(1.1f)
                .setRepPenRange(64)
                .setSingleline(false);
    }

    public static String getTextPrompt(String profession, String timeOfDay, String playerReputation) {
        // J'ai ajouté currentActivity en paramètre car il manquait pour rendre le prompt dynamique
        return """
                ### SYSTEM ROLE
                You are a dynamic NPC Dialogue Generator for the game Minecraft. Your goal is to generate immersive, short, and character-specific lines of dialogue for Villagers encountering a player.
                
                ### CONTEXT
                - **World:** Minecraft (Overworld).
                - **Tone:** Ranging from eager to trade, to grumpy, to scared.
                - **Signature Style:** You MUST occasionally include classic villager noises (e.g., "Hrmm", "Hah!", "Hurgh") in the text.
                
                ### INPUT VARIABLES
                - **Profession:** %s
                - **Time of Day:** %s
                - **Player Reputation:** %s
                
                ### INSTRUCTIONS
                1. Analyze the input variables to determine the mood.
                2. Draft a short response (1-2 sentences max).
                3. If the reputation is low, be rude. If high, be welcoming.
                4. Reference game concepts (emeralds, beds, zombies, golems).
                
                ### OUTPUT FORMAT
                You must respond EXCLUSIVELY with a valid JSON object. No other text before or after.
                Do not include any numbering or extra characters outside the JSON.
                
                Example of valid output:
                {
                  "thought_process": "The villager is happy to see a reputable player.",
                  "mood": "Happy",
                  "dialogue": "Hrmm! Welcome back! Do you have any emeralds to trade today?"
                }
                
                ### CRITICAL CONSTRAINTS
                1. **Output ONLY the JSON object.**
                2. **DO NOT** write "Here is the JSON" or any intro/outro text.
                3. **DO NOT** use Markdown code blocks (no ```json or ```).
                4. Just start with '{' and end with '}'.
                5. Ensure all quotes are properly escaped for a valid JSON string.
                6. Respond ONLY with the JSON.
                """.formatted(profession, timeOfDay, playerReputation);
    }

    /**
     * Extracts the first JSON object found in the input string.
     *
     * @param input The raw string potentially containing JSON.
     * @return The extracted JSON string, or the original input if no JSON object is found.
     */
    public static String extractJson(String input) {
        if (input == null || input.isBlank()) {
            return "{}";
        }

        int firstBrace = input.indexOf('{');
        if (firstBrace == -1) {
            return "{}";
        }

        int count = 0;
        int lastBrace = -1;
        boolean inString = false;
        boolean escaped = false;

        for (int i = firstBrace; i < input.length(); i++) {
            char c = input.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"') {
                inString = !inString;
                continue;
            }

            if (!inString) {
                if (c == '{') {
                    count++;
                } else if (c == '}') {
                    count--;
                    if (count == 0) {
                        lastBrace = i;
                        break;
                    }
                }
            }
        }

        if (lastBrace != -1) {
            return input.substring(firstBrace, lastBrace + 1);
        }

        return input.substring(firstBrace);
    }
}