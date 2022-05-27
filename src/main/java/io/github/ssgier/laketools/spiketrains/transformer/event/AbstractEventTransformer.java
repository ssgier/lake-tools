package io.github.ssgier.laketools.spiketrains.transformer.event;

import io.github.ssgier.laketools.spiketrains.transformer.dto.ChannelSpikeEvent;
import io.github.ssgier.laketools.dto.MarketDataEvent;
import io.github.ssgier.laketools.dto.Quote;
import io.github.ssgier.laketools.dto.Trade;

import java.util.List;

public abstract class AbstractEventTransformer implements EventTransformer {

    @Override
    public List<ChannelSpikeEvent> onMarketDataEvent(MarketDataEvent marketDataEvent) {

        if (marketDataEvent instanceof Quote) {
            return onQuote((Quote) marketDataEvent);
        } else if (marketDataEvent instanceof Trade) {
            return onTrade((Trade) marketDataEvent);
        } else {
            throw new RuntimeException("Market data event is of unsupported type: " + marketDataEvent.getClass());
        }
    }

    abstract protected List<ChannelSpikeEvent> onQuote(Quote quote);

    abstract protected List<ChannelSpikeEvent> onTrade(Trade trade);
}
