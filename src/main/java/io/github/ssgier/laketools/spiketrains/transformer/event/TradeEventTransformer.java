package io.github.ssgier.laketools.spiketrains.transformer.event;

import io.github.ssgier.laketools.spiketrains.transformer.dto.ChannelSpikeEvent;
import io.github.ssgier.laketools.dto.Quote;
import io.github.ssgier.laketools.dto.Trade;
import io.github.ssgier.laketools.spiketrains.transformer.dto.ImmutableChannelSpikeEvent;

import java.util.List;

public class TradeEventTransformer extends AbstractEventTransformer {

    public static long NUM_CHANNELS = 1;

    private final TimeMapper timeMapper;
    private final long channelBase;

    public TradeEventTransformer(TimeMapper timeMapper, long channelBase) {
        this.timeMapper = timeMapper;
        this.channelBase = channelBase;
    }

    @Override
    protected List<ChannelSpikeEvent> onQuote(Quote quote) {
        return List.of();
    }

    @Override
    protected List<ChannelSpikeEvent> onTrade(Trade trade) {
        return List.of(ImmutableChannelSpikeEvent.builder()
                        .time(timeMapper.toTime(trade.exchangeTimestampNanos()))
                        .channelId(channelBase)
                        .build());
    }
}
