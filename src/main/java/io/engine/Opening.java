package io.engine;

import java.util.Optional;
import java.util.stream.Stream;

public enum Opening {
    Engine,
    Random,
    RuyLopez;

    public static Opening getByName(String name){
        Optional<Opening> opening =  Stream.of(Opening.values()).filter(op -> op.name().equals(name)).findFirst();
        return opening.orElse(null);
    }
}
