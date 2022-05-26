package io.github.ssgier.laketools.spiketrains.transformer;

import io.github.ssgier.laketools.dto.MarketDataEvent;

import java.util.List;

public interface Aggregator {
    List<MarketDataEvent> aggregate(TransformerSpecification transformerSpecification);
}
