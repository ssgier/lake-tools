package io.github.ssgier.laketools.loader.polygon;

import io.github.ssgier.laketools.dto.ImmutableQuote;
import io.github.ssgier.laketools.dto.Quote;
import io.github.ssgier.laketools.loader.QuotesLoader;
import io.polygon.kotlin.sdk.rest.stocks.HistoricQuotesParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PolygonQuotesLoader extends AbstractPolygonLoader<Quote> implements QuotesLoader {

    private static final Logger logger = LogManager.getLogger();

    public PolygonQuotesLoader(String apiKey) {
        super(apiKey);
    }

    @Override
    protected List<Quote> loadNextBatch(String ticker, String dateString, Long timestamp) {
        var queryParams = new HistoricQuotesParameters(
                ticker,
                dateString,
                timestamp,
                null,
                false,
                batchSize
        );

        List<Quote> result = polygonRestClient.getStocksClient().getHistoricQuotesBlocking(queryParams).getResults().stream()
                .map(historicQuoteDTO -> ImmutableQuote.builder()
                        .ticker(ticker)
                        .vendorTimestampNanos(historicQuoteDTO.getSipTimestampNanos())
                        .exchangeTimestampNanos(historicQuoteDTO.getExchangeTimestampNanos())
                        .sequenceNumber(historicQuoteDTO.getSequenceNumber())
                        .bidPrice(BigDecimal.valueOf(historicQuoteDTO.getBidPrice()))
                        .bidSize(historicQuoteDTO.getBidSize())
                        .askPrice(BigDecimal.valueOf(historicQuoteDTO.getAskPrice()))
                        .askSize(historicQuoteDTO.getAskSize())
                        .build()).collect(Collectors.toList());

        logger.debug("Fetched batch of {} for ticker {} and date {}", result.size(), ticker, dateString);

        return result;
    }

    @Override
    public Stream<Quote> loadQuotes(String ticker, LocalDate date) {
        return super.loadEvents(ticker, date);
    }
}
