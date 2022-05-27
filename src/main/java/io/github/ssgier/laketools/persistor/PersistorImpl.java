package io.github.ssgier.laketools.persistor;

import io.github.ssgier.laketools.dto.Quote;
import io.github.ssgier.laketools.dto.Trade;
import io.github.ssgier.laketools.spiketrains.transformer.TransformationResult;
import org.apache.commons.csv.CSVFormat;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import static io.github.ssgier.laketools.config.Config.*;

public class PersistorImpl implements Persistor {

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
                QUOTES_FILE_PREFIX,
                QUOTES_HEADER,
                quote -> new Object[]{
                        quote.sequenceNumber(),
                        quote.vendorTimestampNanos(),
                        quote.exchangeTimestampNanos(),
                        quote.bidPrice(),
                        quote.bidSize(),
                        quote.askPrice(),
                        quote.askSize()}
        );
    }

    @Override
    public void persistTrades(String ticker, LocalDate valueDate, Stream<Trade> tradeStream) {
        persistEvents(
                ticker,
                valueDate,
                tradeStream,
                TRADES_FILE_PREFIX,
                TRADES_HEADER,
                trade -> new Object[]{
                        trade.sequenceNumber(),
                        trade.vendorTimestampNanos(),
                        trade.exchangeTimestampNanos(),
                        trade.price(),
                        trade.size()}
        );
    }

    @Override
    public void persistTransformationResult(TransformationResult transformationResult) {
        var now = LocalDateTime.now();
        var directoryPath = Path.of(basePath, "spike_train_data", now.toString());

        persistRecords(
                directoryPath,
                CHANNEL_SPIKES_FILE_NAME,
                transformationResult.getChannelSpikeEvents().stream(),
                CHANNEL_SPIKES_HEADER,
                spikeEvent -> new Object[]{
                        spikeEvent.time(),
                        spikeEvent.channelId()},
                false
                );

        persistRecords(
                directoryPath,
                PRICES_FILE_PREFIX + transformationResult.getTransformationSpec().getTargetTicker(),
                transformationResult.getTargetTickerPriceEvents().stream(),
                PRICES_HEADER,
                priceEvent -> new Object[]{
                        priceEvent.time(),
                        priceEvent.bidPrice(),
                        priceEvent.askPrice()},
                false
                );
    }

    private <T> void persistEvents(
            String ticker,
            LocalDate valueDate,
            Stream<T> eventStream,
            String filePrefix,
            String[] csvHeader,
            Function<T, Object[]> recordItemsExtractor
            ) {
        persistRecords(
                Path.of(basePath, "market_data", valueDate.toString()),
            filePrefix + ticker,
            eventStream,
            csvHeader,
            recordItemsExtractor,
            true
        );
    }

    private <T> void persistRecords(
            Path directoryPath,
            String fileName,
            Stream<T> eventStream,
            String[] csvHeader,
            Function<T, Object[]> recordItemsExtractor,
            boolean withGzip
    ) {
        try {
            var csvFileName = fileName + ".csv";

            if (withGzip) {
                csvFileName += ".gz";
            }

            Path filePath = Path.of(directoryPath.toString(), csvFileName);
            Files.createDirectories(directoryPath);

            OutputStream outPutStream = new BufferedOutputStream(new FileOutputStream(filePath.toFile()));

            if (withGzip) {
                outPutStream = new GZIPOutputStream(outPutStream);
            }

            var sink = new OutputStreamWriter(outPutStream);
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
}
