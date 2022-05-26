package io.github.ssgier.laketools.loader.polygon;

import io.github.ssgier.laketools.dto.MarketDataEvent;
import io.github.ssgier.laketools.loader.util.IncreasingFilter;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractPolygonLoader<T extends MarketDataEvent> {

    private static final int DEFAULT_BATCH_SIZE = 50000;

    protected final PolygonRestClient polygonRestClient;
    protected final int batchSize;

    public AbstractPolygonLoader(String apiKey, int batchSize) {

        if (batchSize < 2) {
            throw new IllegalArgumentException("Batch size must not be less than 2");
        }

        polygonRestClient = new PolygonRestClient(apiKey);
        this.batchSize = batchSize;
    }

    public AbstractPolygonLoader(String apiKey) {
        this(apiKey, DEFAULT_BATCH_SIZE);
    }

    abstract protected List<T> loadNextBatch(String ticker, String dateString, Long timestamp);

    public Stream<T> loadEvents(String ticker, LocalDate date) {

        var dateString = date.toString();
        var sequenceNumberFilter = new IncreasingFilter<>(T::sequenceNumber);
        var exchangeTimestampFilter = new IncreasingFilter<>(T::exchangeTimestampNanos);

        Stream<List<T>> batchStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        new AbstractPolygonLoader<T>.BatchIterator(ticker, dateString), Spliterator.ORDERED),
                false);

        return batchStream
                .flatMap(List::stream)
                .filter(sequenceNumberFilter)
                .filter(exchangeTimestampFilter);
    }

    private final class BatchIterator implements Iterator<List<T>> {

        private final String ticker;
        private final String dateString;
        List<T> nextPrefetched;
        boolean hasNext = true;

        BatchIterator(String ticker, String dateString) {
            this.ticker = ticker;
            this.dateString = dateString;
            nextPrefetched = loadNext(ticker, dateString, null);
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public List<T> next() {

            var prefetched = nextPrefetched;

            nextPrefetched = loadNext(
                    ticker,
                    dateString,
                    prefetched.get(prefetched.size() - 1).vendorTimestampNanos());

            if (nextPrefetched.equals(prefetched)) {

                if (nextPrefetched.size() == batchSize) {
                    throw new RuntimeException(String.format("Assumption of full batch with same nano timestamp is wrong. Ticker: %s, date: %s", ticker, dateString));
                }

                hasNext = false;
            }

            return prefetched;
        }

        private List<T> loadNext(String ticker, String dateString, Long timestamp) {
            return loadNextBatch(ticker, dateString, timestamp);
        }
    }

}
