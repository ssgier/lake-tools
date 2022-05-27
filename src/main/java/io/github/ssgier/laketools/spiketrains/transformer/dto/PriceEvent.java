package io.github.ssgier.laketools.spiketrains.transformer.dto;

import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface PriceEvent {
    double time();
    BigDecimal bidPrice();
    BigDecimal askPrice();
}
