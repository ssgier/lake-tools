package io.github.ssgier.laketools.spiketrains.transformer.dto;

import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface PriceEvent extends TimedEvent {
    BigDecimal bidPrice();
    long bidSize();
    BigDecimal askPrice();
    long askSize();
}
