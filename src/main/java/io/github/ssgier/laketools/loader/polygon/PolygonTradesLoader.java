package io.github.ssgier.laketools.loader.polygon;

import io.github.ssgier.laketools.dto.ImmutableTrade;
import io.github.ssgier.laketools.dto.Trade;
import io.github.ssgier.laketools.loader.TradesLoader;
import io.polygon.kotlin.sdk.rest.stocks.HistoricTradesParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PolygonTradesLoader extends AbstractPolygonLoader<Trade> implements TradesLoader {

    private static final Logger logger = LogManager.getLogger();

    public PolygonTradesLoader(String apiKey) {
        super(apiKey);
    }

    @Override
    public Stream<Trade> loadTrades(String ticker, LocalDate date) {
        return super.loadEvents(ticker, date);
    }

    @Override
    protected List<Trade> loadNextBatch(String ticker, String dateString, Long timestamp) {
        var queryParams = new HistoricTradesParameters(
                ticker,
                dateString,
                timestamp,
                null,
                false,
                batchSize
        );

        List<Trade> result = polygonRestClient.getStocksClient().getHistoricTradesBlocking(queryParams).getResults().stream()
                .map(historicTradeDTO -> ImmutableTrade.builder()
                        .ticker(ticker)
                        .vendorTimestampNanos(historicTradeDTO.getSipTimestampNanos())
                        .exchangeTimestampNanos(historicTradeDTO.getExchangeTimestampNanos())
                        .sequenceNumber(historicTradeDTO.getSequenceNumber())
                        .price(BigDecimal.valueOf(historicTradeDTO.getPrice()))
                        .size(historicTradeDTO.getSize())
                        .build()).collect(Collectors.toList());

        logger.debug("Fetched batch of {} for ticker {} and date {}", result.size(), ticker, dateString);

        return result;
    }
}
