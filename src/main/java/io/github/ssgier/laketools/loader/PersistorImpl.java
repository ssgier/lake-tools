package io.github.ssgier.laketools.loader;

import io.github.ssgier.laketools.dto.MarketDataEvent;
import io.github.ssgier.laketools.dto.Quote;
import io.github.ssgier.laketools.dto.Trade;
import org.apache.commons.csv.CSVFormat;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.function.Function;
import java.util.stream.Stream;

public class PersistorImpl implements Persistor {

    private final static String[] QUOTES_HEADER = {
            "exchangeTimestampNanos",
            "bidPrice",
            "bidSize",
            "askPrice",
            "askSize"
    };
    private final static String[] TRADES_HEADER = {"exchangeTimestampNanos", "price", "size"};

    private final String basePath;

    public PersistorImpl(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void persistQuotes(String ticker, LocalDate valueDate, Stream<Quote> quoteStream) {
        persistEvents(
                ticker,
                valueDate,
                quoteStream,
                "quotes_",
                QUOTES_HEADER,
                quote -> new Object[]{
                        quote.exchangeTimestampNanos(),
                        quote.bidPrice(),
                        quote.bidSize(),
                        quote.askPrice(),
                        quote.askSize()}
        );
    }

    private <T extends MarketDataEvent> void persistEvents(
            String ticker,
            LocalDate valueDate,
            Stream<T> eventStream,
            String filePrefix,
            String[] csvHeader,
            Function<T, Object[]> recordItemsExtractor
            ) {
        try {
            var directoryPath = Path.of(basePath, valueDate.toString());
            Path filePath = Path.of(directoryPath.toString(), filePrefix + ticker + ".csv");
            Files.createDirectories(directoryPath);

            var sink = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(filePath.toFile())));
            var csvPrinter = CSVFormat.Builder.create().setHeader(csvHeader)
                    .build().print(sink);

            eventStream.forEach(event -> {
                try {
                    csvPrinter.printRecord(recordItemsExtractor.apply(event));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            sink.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void persistTrades(String ticker, LocalDate valueDate, Stream<Trade> tradeStream) {
        persistEvents(
                ticker,
                valueDate,
                tradeStream,
                "trades_",
                TRADES_HEADER,
                trade -> new Object[]{trade.exchangeTimestampNanos(), trade.price(), trade.size()}
        );
    }
}
