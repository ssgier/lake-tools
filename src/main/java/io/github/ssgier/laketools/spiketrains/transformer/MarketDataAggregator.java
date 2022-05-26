package io.github.ssgier.laketools.spiketrains.transformer;

import io.github.ssgier.laketools.dto.MarketDataEvent;
import io.github.ssgier.laketools.dto.Quote;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public interface MarketDataAggregator {
    Stream<MarketDataEvent> aggregate(String ticker, List<LocalDate> valueDates);

    Stream<Quote> aggregateQuotes(String ticker, List<LocalDate> valueDates);
}
