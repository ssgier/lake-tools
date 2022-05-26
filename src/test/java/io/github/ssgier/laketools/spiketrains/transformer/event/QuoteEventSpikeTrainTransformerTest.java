package io.github.ssgier.laketools.spiketrains.transformer.event;

import io.github.ssgier.laketools.dto.ImmutableQuote;
import io.github.ssgier.laketools.dto.Quote;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.github.ssgier.laketools.spiketrains.transformer.event.QuoteEventTransformer.*;
import static org.assertj.core.api.Assertions.assertThat;

public class QuoteEventSpikeTrainTransformerTest {

    private static final long OFFSET_TIMESTAMP_NANOS = 1_000_000_000;
    private static final long CHANNEL_BASE = 10;
    private static final Percentage TOLERANCE = Percentage.withPercentage(1e-9);

    private QuoteEventTransformer sut;


    @BeforeEach
    void setup() {
        sut = new QuoteEventTransformer(
                new RelativeTimeMapper(OFFSET_TIMESTAMP_NANOS, 1.0), CHANNEL_BASE);
    }

    @Test
    void noSpikeOnFirstQuote() {
        assertThat(sut.onQuote(makeQuote(1, "1.1", 2)))
                .isEmpty();
    }

    @Test
    void bidAppears() {

        var quote = makeQuote(0, "1.1", 2);

        sut.onQuote(quote);

        quote = ImmutableQuote.builder()
                .from(quote)
                .bidSize(1)
                .exchangeTimestampNanos(getNanoTimestamp(1.1))
                .build();

        var spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasSize(2);

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.1, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_BID_SIZE_INCREASED);
        });

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.1, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_BID_PRICE_INCREASED);
        });
    }

    @Test
    void bidDisappears() {

        var quote = makeQuote(1, "1.1", 2);

        sut.onQuote(quote);

        quote = ImmutableQuote.builder()
                .from(quote)
                .bidSize(0)
                .exchangeTimestampNanos(getNanoTimestamp(1.1))
                .build();

        var spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasSize(2);

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.1, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_BID_SIZE_DECREASED);
        });

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.1, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_BID_PRICE_DECREASED);
        });
    }

    @Test
    void bidSizeIncreases() {

        var quote = makeQuote(1, "1.1", 2);

        sut.onQuote(quote);

        quote = ImmutableQuote.builder()
                .from(quote)
                .bidSize(2)
                .exchangeTimestampNanos(getNanoTimestamp(1.1))
                .build();

        var spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasOnlyOneElementSatisfying(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.1, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_BID_SIZE_INCREASED);
        });
    }

    @Test
    void bidSizeDecreases() {

        var quote = makeQuote(2, "1.1", 2);

        sut.onQuote(quote);

        quote = ImmutableQuote.builder()
                .from(quote)
                .bidSize(1)
                .exchangeTimestampNanos(getNanoTimestamp(1.1))
                .build();

        var spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasOnlyOneElementSatisfying(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.1, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_BID_SIZE_DECREASED);
        });
    }

    @Test
    void bidPriceIncreasesAndThenDecreases() {

        var quote = makeQuote(1, "1.2", 2);

        sut.onQuote(quote);

        quote = ImmutableQuote.builder()
                .from(quote)
                .bidPrice(new BigDecimal("1.1"))
                .exchangeTimestampNanos(getNanoTimestamp(1.2))
                .build();

        var spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasSize(2);

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.2, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_BID_SIZE_DECREASED);
        });

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.2, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_BID_PRICE_INCREASED);
        });

        quote = ImmutableQuote.builder()
                .from(quote)
                .bidPrice(new BigDecimal("1.0"))
                .exchangeTimestampNanos(getNanoTimestamp(1.3))
                .build();

        spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasSize(2);

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.3, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_BID_SIZE_DECREASED);
        });

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.3, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_BID_PRICE_DECREASED);
        });
    }

    @Test
    void askAppears() {

        var quote = makeQuote(1, "1.1", 0);

        sut.onQuote(quote);

        quote = ImmutableQuote.builder()
                .from(quote)
                .askSize(1)
                .exchangeTimestampNanos(getNanoTimestamp(1.2))
                .build();

        var spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasSize(2);

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.2, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_ASK_SIZE_INCREASED);
        });

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.2, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_ASK_PRICE_DECREASED);
        });
    }

    @Test
    void askDisappears() {

        var quote = makeQuote(1, "1.1", 2);

        sut.onQuote(quote);

        quote = ImmutableQuote.builder()
                .from(quote)
                .askSize(0)
                .exchangeTimestampNanos(getNanoTimestamp(1.2))
                .build();

        var spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasSize(2);

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.2, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_ASK_SIZE_DECREASED);
        });

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.2, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_ASK_PRICE_INCREASED);
        });
    }

    @Test
    void askSizeIncreases() {

        var quote = makeQuote(1, "1.1", 2);

        sut.onQuote(quote);

        quote = ImmutableQuote.builder()
                .from(quote)
                .askSize(3)
                .exchangeTimestampNanos(getNanoTimestamp(1.2))
                .build();

        var spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasOnlyOneElementSatisfying(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.2, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_ASK_SIZE_INCREASED);
        });
    }

    @Test
    void askSizeDecreases() {

        var quote = makeQuote(1, "1.1", 2);

        sut.onQuote(quote);

        quote = ImmutableQuote.builder()
                .from(quote)
                .askSize(1)
                .exchangeTimestampNanos(getNanoTimestamp(1.2))
                .build();

        var spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasOnlyOneElementSatisfying(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.2, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_ASK_SIZE_DECREASED);
        });
    }

    @Test
    void askPriceIncreasesAndThenDecreases() {

        var quote = makeQuote(1, "1.2", 2);

        sut.onQuote(quote);

        quote = ImmutableQuote.builder()
                .from(quote)
                .askPrice(new BigDecimal("1.3"))
                .exchangeTimestampNanos(getNanoTimestamp(1.2))
                .build();

        var spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasSize(2);

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.2, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_ASK_SIZE_DECREASED);
        });

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.2, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_ASK_PRICE_INCREASED);
        });

        quote = ImmutableQuote.builder()
                .from(quote)
                .askPrice(new BigDecimal("1.2"))
                .exchangeTimestampNanos(getNanoTimestamp(1.3))
                .build();

        spikeEvents = sut.onQuote(quote);

        assertThat(spikeEvents).hasSize(2);

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.3, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_ASK_SIZE_DECREASED);
        });

        assertThat(spikeEvents).anySatisfy(spikeEvent -> {
            assertThat(spikeEvent.time()).isCloseTo(1.3, TOLERANCE);
            assertThat(spikeEvent.channelId()).isEqualTo(CHANNEL_BASE + CHANNEL_OFFSET_ASK_PRICE_DECREASED);
        });
    }

    private static long getNanoTimestamp(double time) {
        return (long)(OFFSET_TIMESTAMP_NANOS + 1e9 * time);
    }

    private static Quote makeQuote(
            long bidSize,
            String askPriceString,
            long askSize
    ) {
        long timestampNanos = getNanoTimestamp(2.0);

        return ImmutableQuote.builder()
                .ticker("TEST_TICKER")
                .vendorTimestampNanos(timestampNanos)
                .exchangeTimestampNanos(timestampNanos)
                .sequenceNumber(timestampNanos)
                .bidPrice(new BigDecimal("1.0"))
                .bidSize(bidSize)
                .askPrice(new BigDecimal(askPriceString))
                .askSize(askSize)
                .build();
    }
}
