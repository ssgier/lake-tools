package io.github.ssgier.laketools.spiketrains.transformer.event;

import io.github.ssgier.laketools.spiketrains.transformer.dto.ChannelSpikeEvent;
import io.github.ssgier.laketools.dto.MarketDataEvent;

import java.util.List;

public interface EventTransformer {
    List<ChannelSpikeEvent> onMarketDataEvent(MarketDataEvent marketDataEvent);
}
