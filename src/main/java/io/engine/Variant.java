package io.engine;

import java.util.Optional;
import java.util.stream.Stream;

public enum Variant {
    Chess,
    Chess960,
    Atomic;

    public static Variant getByName(String name){
        Optional<Variant> var =  Stream.of(Variant.values()).filter(variant -> variant.name().equals(name)).findFirst();
        return var.orElse(null);
    }
}
