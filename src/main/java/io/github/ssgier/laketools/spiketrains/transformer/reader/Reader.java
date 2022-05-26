package io.github.ssgier.laketools.spiketrains.transformer.reader;

import io.github.ssgier.laketools.dto.Quote;
import io.github.ssgier.laketools.dto.Trade;

import java.time.LocalDate;
import java.util.stream.Stream;

public interface Reader {
    Stream<Quote> readQuotes(String ticker, LocalDate valueDate);

    Stream<Trade> readTrades(String ticker, LocalDate valueDate);
}
