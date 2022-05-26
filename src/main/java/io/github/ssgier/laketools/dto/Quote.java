package io.github.ssgier.laketools.dto;

import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface Quote extends MarketDataEvent {
    BigDecimal bidPrice();
    long bidSize();
    BigDecimal askPrice();
    long askSize();
}
