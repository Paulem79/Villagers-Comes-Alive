package net.paulem.vca.dialogs;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Function;

/**
 * Represents a dynamic dialog that can be customized based on a title generator and other configuration options.<br>
 * This dialog is designed to handle various contexts and allows the use of dynamic buttons that are displayed
 * conditionally, based on the provided context.
 * <p>
 * This class is experimental and may change in the future.
 *
 * @param <H> The type of the object used to compute the dialog title.
 * @param <T> The type of the context that affects the dialog's behavior and buttons.
 */
@ApiStatus.Experimental
public record DynamicDialog<H, T>(Function<H, Component> titleFunc, boolean canCloseWithEscape) {
    public Dialog create(H titler, T context, List<Component> body, List<DynamicButton<T>> buttons) {
        List<PlainMessageDialogBody> bodies = body.stream().map(DialogBody::plainMessage).toList();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(titleFunc().apply(titler))
                        .body(bodies)
                        .canCloseWithEscape(canCloseWithEscape())
                        .build())

                .type(DialogType.multiAction(buttons.stream()
                        .map(tDynamicButton -> tDynamicButton.create(context))
                        .filter(button -> button != null)
                        .toList()

                ).build())
        );
    }

}
