package io.github.ssgier.laketools.loader;

import java.time.LocalDate;

public class DownloadJob implements Runnable {
    private final QuotesLoader quotesLoader;
    private final TradesLoader tradesLoader;
    private final Persistor persistor;
    private final String ticker;
    private final LocalDate valueDate;

    public DownloadJob(QuotesLoader quotesLoader, TradesLoader tradesLoader, Persistor persistor, String ticker, LocalDate valueDate) {
        this.quotesLoader = quotesLoader;
        this.tradesLoader = tradesLoader;
        this.persistor = persistor;
        this.ticker = ticker;
        this.valueDate = valueDate;
    }

    @Override
    public void run() {
        persistor.persistQuotes(ticker, valueDate, quotesLoader.loadQuotes(ticker, valueDate));
        persistor.persistTrades(ticker, valueDate, tradesLoader.loadTrades(ticker, valueDate));
    }
}
