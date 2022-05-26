package io.github.ssgier.laketools.loader;

import io.github.ssgier.laketools.dto.Trade;

import java.time.LocalDate;
import java.util.stream.Stream;

public interface TradesLoader {
    Stream<Trade> loadTrades(String ticker, LocalDate date);
}
