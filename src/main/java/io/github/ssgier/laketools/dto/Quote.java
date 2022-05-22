package io.github.ssgier.laketools.dto;

import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface Quote {
    String ticker();
    long exchangeTsNanos();
    BigDecimal bidPrice();
    long bidSize();
    BigDecimal askPrice();
    long askSize();
}
