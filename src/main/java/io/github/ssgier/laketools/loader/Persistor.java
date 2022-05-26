package io.github.ssgier.laketools.loader;

import io.github.ssgier.laketools.dto.Quote;
import io.github.ssgier.laketools.dto.Trade;

import java.time.LocalDate;
import java.util.stream.Stream;

public interface Persistor {
    void persistQuotes(String ticker, LocalDate valueDate, Stream<Quote> quoteStream);
    void persistTrades(String ticker, LocalDate valueDate, Stream<Trade> tradeStream);
}
