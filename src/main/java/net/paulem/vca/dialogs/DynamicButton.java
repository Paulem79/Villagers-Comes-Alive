package net.paulem.vca.dialogs;

import io.papermc.paper.registry.data.dialog.ActionButton;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Represents a configurable button within a dynamic dialog. A DynamicButton is used to display
 * a button conditionally based on the provided context and to define the associated action
 * that will execute when the button is pressed.
 *
 * <p>
 * Components:<br>
 * - `condition`: A function that determines if the button should be visible
 *   based on the given context. If this condition is null, the button is always visible.<br>
 * - `action`: Defines the behavior that occurs when the button is clicked. This is
 *   represented by a {@link DynamicDialogAction}.<br>
 * - `title`: The display title of the button.<br>
 * - `width`: The width of the button as it appears in the dialog.
 * <p>
 * Key Functionalities:<br>
 * - Visibility: The button's visibility is controlled dynamically via the `condition` function.<br>
 * - Action Generation: Creates an actionable button with the appropriate context
 *   and action setup.
 *
 * @param <T> The type of the context object that determines the button's visibility and provides
 *            additional data for the button's action.
 */
public record DynamicButton<T>(Function<T, Boolean> condition, @Nullable DynamicDialogAction<T> action, Component title,
                               int width) {
    public boolean shouldShow(T context) {
        return condition() == null || condition().apply(context);
    }

    @Nullable
    public ActionButton create(T context) {
        if (!shouldShow(context)) return null;

        ActionButton.Builder builder = ActionButton.builder(title())
                .width(width());

        if(action() != null) {
            builder.action(action().create(context));
        }

        return builder.build();
    }
}
