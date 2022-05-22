package io.github.ssgier.laketools.dto;

import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface Trade {
    String ticker();
    long exchangeTsNanos();
    BigDecimal price();
    long size();
}
