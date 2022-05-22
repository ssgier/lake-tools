package io.github.ssgier.laketools.loader;

import io.github.ssgier.laketools.dto.ImmutableQuote;
import io.github.ssgier.laketools.dto.Quote;
import io.github.ssgier.laketools.util.Util;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import io.polygon.kotlin.sdk.rest.stocks.HistoricQuoteDTO;
import io.polygon.kotlin.sdk.rest.stocks.HistoricQuotesDTO;
import io.polygon.kotlin.sdk.rest.stocks.HistoricQuotesParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PolygonQuotesLoader implements QuotesLoader {

    private static final Logger logger = LogManager.getLogger();

    private static final int DEFAULT_BATCH_SIZE = 100;

    private final PolygonRestClient polygonRestClient;
    private final int batchSize;

    public PolygonQuotesLoader(String apiKey, int batchSize) {

        if (batchSize < 2) {
            throw new IllegalArgumentException("Batch size must not be less than 2");
        }

        polygonRestClient = new PolygonRestClient(apiKey);
        this.batchSize = batchSize;
    }

    public PolygonQuotesLoader(String apiKey) {
        this(apiKey, DEFAULT_BATCH_SIZE);
    }

    @Override
    public Stream<Quote> loadQuotes(String ticker, LocalDate date) {

        var dateString = date.toString();

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                                new BatchIterator(ticker, dateString), Spliterator.ORDERED),
                        false)
                .flatMap(historicQuotesDTO -> historicQuotesDTO.getResults().stream())
                .filter(Util.distinctBy(HistoricQuoteDTO::getSipTimestampNanos))
                .map(historicQuoteDTO -> ImmutableQuote.builder()
                        .ticker(ticker)
                        .exchangeTsNanos(historicQuoteDTO.getExchangeTimestampNanos())
                        .bidPrice(BigDecimal.valueOf(historicQuoteDTO.getBidPrice()))
                        .bidSize(historicQuoteDTO.getBidSize())
                        .askPrice(BigDecimal.valueOf(historicQuoteDTO.getAskPrice()))
                        .askSize(historicQuoteDTO.getAskSize())
                        .build());
    }

    private final class BatchIterator implements Iterator<HistoricQuotesDTO> {

        private final String ticker;
        private final String dateString;
        HistoricQuotesDTO nextPrefetched;

        BatchIterator(String ticker, String dateString) {
            this.ticker = ticker;
            this.dateString = dateString;
            nextPrefetched = loadNext(ticker, dateString, null);
        }

        @Override
        public boolean hasNext() {
            return !nextPrefetched.getResults().isEmpty();
        }

        @Override
        public HistoricQuotesDTO next() {

            var prefetched = nextPrefetched;

            nextPrefetched = loadNext(
                    ticker,
                    dateString,
                    prefetched.getResults().get(prefetched.getResults().size() - 1).getSipTimestampNanos());

            logger.debug("Fetched batch of {} quotes", nextPrefetched.getResults().size());

            return prefetched;
        }

        private HistoricQuotesDTO loadNext(String ticker, String dateString, Long timestamp) {

            var queryParams = new HistoricQuotesParameters(
                    ticker,
                    dateString,
                    timestamp,
                    null,
                    false,
                    batchSize
            );

            return polygonRestClient.getStocksClient().getHistoricQuotesBlocking(queryParams);
        }
    }
}
