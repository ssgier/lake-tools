package io.github.ssgier.laketools.spiketrains.transformer;

import io.github.ssgier.laketools.dto.MarketDataEvent;
import io.github.ssgier.laketools.dto.Quote;
import io.github.ssgier.laketools.dto.Trade;
import io.github.ssgier.laketools.loader.util.IncreasingFilter;
import io.github.ssgier.laketools.spiketrains.transformer.reader.Reader;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AggregatorImpl implements Aggregator {

    private final Reader reader;

    public AggregatorImpl(Reader reader) {
        this.reader = reader;
    }

    @Override
    public List<MarketDataEvent> aggregate(TransformerSpecification transformerSpecification) {

        return transformerSpecification.getInputItems().stream()
                .flatMap(inputItem -> {

                    var quotesStream = reader.readQuotes(inputItem.getTicker(), inputItem.getValueDate());
                    var tradesStream = reader.readTrades(inputItem.getTicker(), inputItem.getValueDate());

                    return Stream.concat(quotesStream.filter(makeQuoteFilter()), tradesStream.filter(makeTradeFilter()));
                }).sorted(Comparator.comparing(MarketDataEvent::exchangeTimestampNanos)).collect(Collectors.toList());
    }

    private static Predicate<Quote> makeQuoteFilter() {
        Predicate<Quote> noCrossedBookFilter = quote -> quote.bidSize() == 0 || quote.askSize() == 0 ||
                quote.askPrice().compareTo(quote.bidPrice()) > 0;

        return AggregatorImpl.<Quote>makeMarketDataFilter().and(noCrossedBookFilter);
    }

    private static Predicate<Trade> makeTradeFilter() {
        return makeMarketDataFilter();
    }

    private static <T extends MarketDataEvent> Predicate<T> makeMarketDataFilter() {
        var sequenceNumberFilter = new IncreasingFilter<>(T::sequenceNumber);
        var exchangeTimestampFilter = new IncreasingFilter<>(T::exchangeTimestampNanos);

        return sequenceNumberFilter.and(exchangeTimestampFilter);
    }
}
