package io.github.ssgier.laketools.spiketrains.transformer.event;

import io.github.ssgier.laketools.spiketrains.transformer.dto.ChannelSpikeEvent;
import io.github.ssgier.laketools.dto.Quote;
import io.github.ssgier.laketools.dto.Trade;
import io.github.ssgier.laketools.spiketrains.transformer.dto.ImmutableChannelSpikeEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class QuoteEventTransformer extends AbstractEventTransformer {

    public static long NUM_CHANNELS = 8;
    public static int CHANNEL_OFFSET_BID_PRICE_INCREASED = 0;
    public static int CHANNEL_OFFSET_BID_PRICE_DECREASED = 1;
    public static int CHANNEL_OFFSET_BID_SIZE_INCREASED = 2;
    public static int CHANNEL_OFFSET_BID_SIZE_DECREASED = 3;
    public static int CHANNEL_OFFSET_ASK_PRICE_INCREASED = 4;
    public static int CHANNEL_OFFSET_ASK_PRICE_DECREASED = 5;
    public static int CHANNEL_OFFSET_ASK_SIZE_INCREASED = 6;
    public static int CHANNEL_OFFSET_ASK_SIZE_DECREASED = 7;


    private final TimeMapper timeMapper;
    private final long channelBase;
    private final Map<Integer, Predicate<Quote>> channelTesters = Map.of(
            CHANNEL_OFFSET_BID_PRICE_INCREASED, this::hasBidPriceIncreased,
            CHANNEL_OFFSET_BID_PRICE_DECREASED, this::hasBidPriceDecreased,
            CHANNEL_OFFSET_BID_SIZE_INCREASED, this::hasBidSizeIncreased,
            CHANNEL_OFFSET_BID_SIZE_DECREASED, this::hasBidSizeDecreased,
            CHANNEL_OFFSET_ASK_PRICE_INCREASED, this::hasAskPriceIncreased,
            CHANNEL_OFFSET_ASK_PRICE_DECREASED, this::hasAskPriceDecreased,
            CHANNEL_OFFSET_ASK_SIZE_INCREASED, this::hasAskSizeIncreased,
            CHANNEL_OFFSET_ASK_SIZE_DECREASED, this::hasAskSizeDecreased
    );

    private Quote lastQuote = null;

    public QuoteEventTransformer(TimeMapper timeMapper, long channelBase) {

        assert channelTesters.size() == NUM_CHANNELS;

        this.timeMapper = timeMapper;
        this.channelBase = channelBase;
    }

    @Override
    protected List<ChannelSpikeEvent> onQuote(Quote quote) {

        List<ChannelSpikeEvent> spikeEvents = new ArrayList<>();

        if (lastQuote != null) {

            for (var entry : channelTesters.entrySet()) {
                int channelOffset = entry.getKey();
                var tester = entry.getValue();

                if (tester.test(quote)) {
                    spikeEvents.add(ImmutableChannelSpikeEvent.builder()
                            .time(timeMapper.toTime(quote.exchangeTimestampNanos()))
                            .channelId(channelBase + channelOffset)
                            .build());
                }
            }
        }

        lastQuote = quote;

        return spikeEvents;
    }

    @Override
    protected List<ChannelSpikeEvent> onTrade(Trade trade) {
        return List.of();
    }

    private static boolean hasSizeIncreased(BigDecimal fromPrice, long fromSize, BigDecimal toPrice, long toSize) {
        return toSize > 0 && toSize > fromSize && (fromSize <= 0 || toPrice.compareTo(fromPrice) == 0);
    }

    private static boolean hasSizeDecreased(BigDecimal fromPrice, long fromSize, BigDecimal toPrice, long toSize) {
        return fromSize > 0 && (toSize < fromSize || toPrice.compareTo(fromPrice) != 0);
    }

    private boolean hasBidPriceIncreased(Quote quote) {
        return quote.bidSize() > 0 && (lastQuote.bidSize() <= 0 || quote.bidPrice().compareTo(lastQuote.bidPrice()) > 0);
    }

    private boolean hasBidPriceDecreased(Quote quote) {
        return lastQuote.bidSize() > 0 && (quote.bidSize() <= 0 || quote.bidPrice().compareTo(lastQuote.bidPrice()) < 0);
    }

    private boolean hasBidSizeIncreased(Quote quote) {
        return hasSizeIncreased(lastQuote.bidPrice(), lastQuote.bidSize(), quote.bidPrice(), quote.bidSize());
    }

    private boolean hasBidSizeDecreased(Quote quote) {
        return hasSizeDecreased(lastQuote.bidPrice(), lastQuote.bidSize(), quote.bidPrice(), quote.bidSize());
    }

    private boolean hasAskPriceIncreased(Quote quote) {
        return lastQuote.askSize() > 0 && (quote.askSize() <= 0 || quote.askPrice().compareTo(lastQuote.askPrice()) > 0);
    }

    private boolean hasAskPriceDecreased(Quote quote) {
        return quote.askSize() > 0 && (lastQuote.askSize() <= 0 || quote.askPrice().compareTo(lastQuote.askPrice()) < 0);
    }

    private boolean hasAskSizeIncreased(Quote quote) {
        return hasSizeIncreased(lastQuote.askPrice(), lastQuote.askSize(), quote.askPrice(), quote.askSize());
    }

    private boolean hasAskSizeDecreased(Quote quote) {
        return hasSizeDecreased(lastQuote.askPrice(), lastQuote.askSize(), quote.askPrice(), quote.askSize());
    }
}
