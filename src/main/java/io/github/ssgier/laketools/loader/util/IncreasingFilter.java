package io.github.ssgier.laketools.loader.util;

import io.github.ssgier.laketools.dto.MarketDataEvent;

import java.util.function.Function;
import java.util.function.Predicate;

public final class IncreasingFilter<T extends MarketDataEvent> implements Predicate<T> {
    private boolean firstSeen = false;
    private long lastSeen = 0;
    private final Function<T, Long> numberExtractor;

    public IncreasingFilter(Function<T, Long> numberExtractor) {
        this.numberExtractor = numberExtractor;
    }

    @Override
    public boolean test(T event) {

        long numberValue = numberExtractor.apply(event);
        if (firstSeen) {
            if (lastSeen >= numberValue) {
                return false;
            }
        } else {
            firstSeen = true;
        }
        lastSeen = numberValue;
        return true;
    }
}
