package io.github.ssgier.laketools.loader;

import io.github.ssgier.laketools.dto.Quote;

import java.time.LocalDate;
import java.util.stream.Stream;

public interface QuotesLoader {
    Stream<Quote> loadQuotes(String ticker, LocalDate date);
}
