package io.github.ssgier.laketools.spiketrains.transformer;

import io.github.ssgier.laketools.dto.ChannelSpikeEvent;
import io.github.ssgier.laketools.spiketrains.transformer.event.QuoteEventTransformer;
import io.github.ssgier.laketools.spiketrains.transformer.event.TimeMapper;
import io.github.ssgier.laketools.spiketrains.transformer.event.TradeEventTransformer;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class SpikeTrainTransformerImpl implements SpikeTrainTransformer {

    private final MarketDataAggregator marketDataAggregator;
    private final TimeMapper timeMapper;

    public SpikeTrainTransformerImpl(MarketDataAggregator marketDataAggregator, TimeMapper timeMapper) {
        this.marketDataAggregator = marketDataAggregator;
        this.timeMapper = timeMapper;
    }

    private Map<String, Long> computeTickerToChannelBaseMapping(Collection<String> tickers) {
        var numChannelsPerTicker = QuoteEventTransformer.NUM_CHANNELS + TradeEventTransformer.NUM_CHANNELS;

        long nextChannelBase = 0;
        Map<String, Long> tickerToChannelBase = new HashMap<>();

        for (var ticker : tickers) {

            tickerToChannelBase.put(ticker, nextChannelBase);

            nextChannelBase += numChannelsPerTicker;
        }

        return tickerToChannelBase;
    }

    @Override
    public List<ChannelSpikeEvent> transform(TransformationSpec transformationSpec) {

        Map<String, List<LocalDate>> valueDatesByTicker = new HashMap<>();

        transformationSpec.getInputItems().forEach(item ->
                valueDatesByTicker
                        .computeIfAbsent(item.getTicker(), ignored -> new ArrayList<>())
                        .add(item.getValueDate()));

        var tickerToChannelBase = computeTickerToChannelBaseMapping(valueDatesByTicker.keySet());

        return tickerToChannelBase.entrySet().stream().flatMap(entry -> {

            var ticker = entry.getKey();
            var channelBase = entry.getValue();
            var channelBaseTradeEventTransformer = channelBase + QuoteEventTransformer.NUM_CHANNELS;
            var valueDates = valueDatesByTicker.get(ticker);

            var eventTransformers = List.of(
                    new QuoteEventTransformer(timeMapper, channelBase),
                    new TradeEventTransformer(timeMapper, channelBaseTradeEventTransformer)
            );

            return marketDataAggregator.aggregate(ticker, valueDates)
                    .flatMap(marketDataEvent ->
                            eventTransformers.stream().flatMap(eventTransformer -> eventTransformer.onMarketDataEvent(marketDataEvent).stream()));
        }).sorted(Comparator.comparing(ChannelSpikeEvent::time)).collect(Collectors.toList());
    }
}
