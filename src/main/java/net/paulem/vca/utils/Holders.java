package net.paulem.vca.utils;

public final class Holders {
    private Holders() {}

    public record Holder<T>(T value) {}

    public record Holder2<T, U>(T value1, U value2) {}

    public record Holder3<T, U, V>(T value1, U value2, V value3) {}

    public record Holder4<T, U, V, W>(T value1, U value2, V value3, W value4) {}

    public record Holder5<T, U, V, W, X>(T value1, U value2, V value3, W value4, X value5) {}

    public record Holder6<T, U, V, W, X, Y>(T value1, U value2, V value3, W value4, X value5, Y value6) {}

    public record Holder7<T, U, V, W, X, Y, Z>(T value1, U value2, V value3, W value4, X value5, Y value6, Z value7) {}

    public record Holder8<T, U, V, W, X, Y, Z, A>(T value1, U value2, V value3, W value4, X value5, Y value6, Z value7, A value8) {}
}
