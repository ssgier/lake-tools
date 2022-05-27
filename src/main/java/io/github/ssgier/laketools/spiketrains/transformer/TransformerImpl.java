package io.github.ssgier.laketools.spiketrains.transformer;

import io.github.ssgier.laketools.spiketrains.transformer.dto.ImmutablePriceEvent;
import io.github.ssgier.laketools.spiketrains.transformer.dto.PriceEvent;
import io.github.ssgier.laketools.spiketrains.transformer.event.TimeMapper;

import java.util.List;
import java.util.stream.Collectors;

public class TransformerImpl implements Transformer {

    private final MarketDataAggregator marketDataAggregator;
    private final SpikeTrainTransformer spikeTrainTransformer;
    private final TimeMapper timeMapper;

    public TransformerImpl(MarketDataAggregator marketDataAggregator, SpikeTrainTransformer spikeTrainTransformer, TimeMapper timeMapper) {
        this.marketDataAggregator = marketDataAggregator;
        this.spikeTrainTransformer = spikeTrainTransformer;
        this.timeMapper = timeMapper;
    }

    @Override
    public TransformationResult process(TransformationSpec transformationSpec) {
        return new TransformationResult(
                transformationSpec,
                spikeTrainTransformer.transform(transformationSpec),
                computePriceEvents(transformationSpec));
    }

    private List<PriceEvent> computePriceEvents(TransformationSpec transformationSpec) {
        var valueDates = transformationSpec.getInputItems().stream()
                .map(TransformationSpec.InputItem::getValueDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return marketDataAggregator.aggregateQuotes(transformationSpec.getTargetTicker(), valueDates)
                .map(quote -> ImmutablePriceEvent.builder()
                        .time(timeMapper.toTime(quote.exchangeTimestampNanos()))
                        .bidPrice(quote.bidPrice())
                        .askPrice(quote.askPrice())
                        .build())
                .filter(priceEvent -> priceEvent.time() >= 0)
                .collect(Collectors.toList());
    }
}
