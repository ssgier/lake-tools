package io.github.ssgier.laketools.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class Util {
    public static <T> Predicate<T> distinctBy(Function<? super T, ?> valueExtractor) {
        Set<Object> seenValues = new HashSet<>();
        return t -> seenValues.add(valueExtractor.apply(t));
    }
}
