package io.github.ssgier.laketools.spiketrains.transformer;

import io.github.ssgier.laketools.dto.MarketDataEvent;
import io.github.ssgier.laketools.dto.Quote;
import io.github.ssgier.laketools.dto.Trade;
import io.github.ssgier.laketools.loader.util.IncreasingFilter;
import io.github.ssgier.laketools.spiketrains.transformer.reader.Reader;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MarketDataAggregatorImpl implements MarketDataAggregator {

    private final Reader reader;

    public MarketDataAggregatorImpl(Reader reader) {
        this.reader = reader;
    }

    @Override
    public Stream<MarketDataEvent> aggregate(String ticker, List<LocalDate> valueDates) {

        return valueDates.stream().flatMap(valueDate -> {
            var quotesStream = reader.readQuotes(ticker, valueDate);
            var tradesStream = reader.readTrades(ticker, valueDate);

            return Stream.concat(quotesStream.filter(makeQuoteFilter()), tradesStream.filter(makeTradeFilter()));
        }).sorted(Comparator.comparing(MarketDataEvent::exchangeTimestampNanos));
    }

    @Override
    public Stream<Quote> aggregateQuotes(String ticker, List<LocalDate> valueDatesSorted) {
        return valueDatesSorted.stream().flatMap(valueDate -> reader.readQuotes(ticker, valueDate)).filter(makeQuoteFilter());
    }

    private static Predicate<Quote> makeQuoteFilter() {
        Predicate<Quote> noCrossedBookFilter = quote -> quote.bidSize() == 0 || quote.askSize() == 0 ||
                quote.askPrice().compareTo(quote.bidPrice()) > 0;

        return MarketDataAggregatorImpl.<Quote>makeMarketDataFilter().and(noCrossedBookFilter);
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
