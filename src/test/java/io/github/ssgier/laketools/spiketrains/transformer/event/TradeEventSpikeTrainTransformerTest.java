package io.github.ssgier.laketools.spiketrains.transformer.event;

import io.github.ssgier.laketools.dto.ImmutableTrade;
import io.github.ssgier.laketools.dto.Trade;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class TradeEventSpikeTrainTransformerTest {

    private TradeEventTransformer sut;

    @BeforeEach
    void setup() {
        sut = new TradeEventTransformer(new RelativeTimeMapper(100_000_000, 1.0), 10);
    }

    @Test
    void onTrade() {
        var result = sut.onTrade(makeTrade());
        assertThat(result).hasOnlyOneElementSatisfying(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(0.9, Percentage.withPercentage(1e-9));
            assertThat(spikeEvent.channelId()).isEqualTo(10);
        });

    }

    private static Trade makeTrade() {
        return ImmutableTrade.builder()
                .ticker("TEST_TICKER")
                .vendorTimestampNanos(1000000000)
                .exchangeTimestampNanos(1000000000)
                .sequenceNumber(1000000000)
                .price(BigDecimal.ONE)
                .size(2)
                .build();
    }
}
