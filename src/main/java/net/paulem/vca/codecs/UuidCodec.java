package net.paulem.vca.codecs;

import net.kyori.adventure.util.Codec;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class UuidCodec implements Codec<UUID, String, RuntimeException, RuntimeException> {
    public static final UuidCodec INSTANCE = new UuidCodec();

    @Override
    public @NonNull String encode(@NonNull UUID input) throws RuntimeException {
        return "{uuid:\"" + input + "\"}";
    }

    @Override
    public @NonNull UUID decode(@NonNull String input) throws RuntimeException {
        return UUID.fromString(input.replace("{uuid:\"", "").replace("\"}", ""));
    }
}
