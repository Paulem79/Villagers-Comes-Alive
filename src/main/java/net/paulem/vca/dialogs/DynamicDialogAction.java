package net.paulem.vca.dialogs;

import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a dynamic action that can be executed in association with a dialog button.
 * This action utilizes a key for identification, an execution logic defined by a consumer,
 * and an optional function to add additional data to the dialog action.
 *
 * @param <T> The type used to generate additional context or data for the dialog action.
 */
public record DynamicDialogAction<T>(Key key, Consumer<PlayerCustomClickEvent> action,
                                     @Nullable Function<T, BinaryTagHolder> additionsFunc) {
    public static final Map<Key, DynamicDialogAction<?>> REGISTRY = new HashMap<>();

    public DynamicDialogAction(Key key, Consumer<PlayerCustomClickEvent> action, @Nullable Function<T, BinaryTagHolder> additionsFunc) {
        this.key = key;
        this.action = action;
        this.additionsFunc = additionsFunc;

        register();
    }

    public void register() {
        REGISTRY.put(key(), this);
    }

    public DialogAction create(T additions) {
        return DialogAction.customClick(key(), additionsFunc() != null ? additionsFunc().apply(additions) : null);
    }

    public void execute(PlayerCustomClickEvent event) {
        action().accept(event);
    }
}
