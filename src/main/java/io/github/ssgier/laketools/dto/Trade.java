package io.github.ssgier.laketools.dto;

import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface Trade extends MarketDataEvent {
    BigDecimal price();
    long size();
}
