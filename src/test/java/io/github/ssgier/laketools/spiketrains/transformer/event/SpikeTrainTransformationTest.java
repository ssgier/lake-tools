package io.github.ssgier.laketools.spiketrains.transformer.event;

import io.github.ssgier.laketools.dto.ImmutableQuote;
import io.github.ssgier.laketools.dto.ImmutableTrade;
import io.github.ssgier.laketools.spiketrains.transformer.MarketDataAggregatorImpl;
import io.github.ssgier.laketools.spiketrains.transformer.SpikeTrainTransformerImpl;
import io.github.ssgier.laketools.spiketrains.transformer.TransformationSpec;
import io.github.ssgier.laketools.spiketrains.transformer.reader.Reader;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpikeTrainTransformationTest {

    private final static String TICKER_0 = "TICKER_0";
    private final static String TICKER_1 = "TICKER_1";
    private final static LocalDate VALUE_DATE_0 = LocalDate.of(2022, 5, 26);
    private final static LocalDate VALUE_DATE_1 = LocalDate.of(2022, 5, 27);
    private final static long TIMESTAMP_0 = (long)1e9;
    private final static long TIMESTAMP_1 = (long)1e9 * 3600 * 24;
    private final static Percentage TOLERANCE = Percentage.withPercentage(1e-9);

    @Test
    void simpleScenario() {
        var reader = mock(Reader.class);

        var quote0 = ImmutableQuote.builder()
                .ticker(TICKER_1)
                .exchangeTimestampNanos(TIMESTAMP_0)
                .vendorTimestampNanos(TIMESTAMP_0)
                .sequenceNumber(TIMESTAMP_0)
                .bidPrice(BigDecimal.valueOf(1.0))
                .bidSize(2)
                .askPrice(BigDecimal.valueOf(1.1))
                .askSize(2)
                .build();

        var quote1 = ImmutableQuote.builder()
                .from(quote0)
                .exchangeTimestampNanos(TIMESTAMP_1)
                .vendorTimestampNanos(TIMESTAMP_1)
                .sequenceNumber(TIMESTAMP_1)
                .bidSize(1)
                .build();

        var trade = ImmutableTrade.builder()
                .from(quote1)
                .price(BigDecimal.valueOf(1.0))
                .size(1)
                .build();

        when(reader.readQuotes(eq(TICKER_0), eq(VALUE_DATE_0))).thenReturn(Stream.empty());
        when(reader.readTrades(eq(TICKER_0), eq(VALUE_DATE_0))).thenReturn(Stream.empty());
        when(reader.readQuotes(eq(TICKER_0), eq(VALUE_DATE_1))).thenReturn(Stream.empty());
        when(reader.readTrades(eq(TICKER_0), eq(VALUE_DATE_1))).thenReturn(Stream.empty());

        when(reader.readQuotes(eq(TICKER_1), eq(VALUE_DATE_0))).thenReturn(Stream.of(quote0));
        when(reader.readTrades(eq(TICKER_1), eq(VALUE_DATE_0))).thenReturn(Stream.empty());
        when(reader.readQuotes(eq(TICKER_1), eq(VALUE_DATE_1))).thenReturn(Stream.of(quote1));
        when(reader.readTrades(eq(TICKER_1), eq(VALUE_DATE_1))).thenReturn(Stream.of(trade));

        var marketDataAggregator = new MarketDataAggregatorImpl(reader);
        var spikeTrainTransformer = new SpikeTrainTransformerImpl(marketDataAggregator,
                new RelativeTimeMapper(0, 1));

        var transformationSpec = new TransformationSpec(
                List.of(
                        new TransformationSpec.InputItem(TICKER_0, VALUE_DATE_0),
                        new TransformationSpec.InputItem(TICKER_0, VALUE_DATE_1),
                        new TransformationSpec.InputItem(TICKER_1, VALUE_DATE_1),
                        new TransformationSpec.InputItem(TICKER_1, VALUE_DATE_0)
                ), TICKER_0);

        var result = spikeTrainTransformer.transform(transformationSpec);

        assertThat(result).hasSize(2);

        assertThat(result).allSatisfy(spikeEvent ->
                assertThat(spikeEvent.time()).isCloseTo(TIMESTAMP_1 * 1e-9, TOLERANCE));
        assertThat(result).anySatisfy(spikeEvent ->
                assertThat(spikeEvent.channelId()).isEqualTo(QuoteEventTransformer.CHANNEL_OFFSET_BID_SIZE_DECREASED));
        assertThat(result).anySatisfy(spikeEvent ->
                assertThat(spikeEvent.channelId()).isEqualTo(QuoteEventTransformer.NUM_CHANNELS));
    }
}
