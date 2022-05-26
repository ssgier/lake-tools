package io.github.ssgier.laketools.spiketrains.transformer.reader;

import io.github.ssgier.laketools.dto.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import static io.github.ssgier.laketools.config.Config.*;

public class ReaderImpl implements Reader {

    private final String basePath;

    public ReaderImpl(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public Stream<Quote> readQuotes(String ticker, LocalDate valueDate) {
        return readEvents(
                ticker,
                valueDate,
                QUOTES_FILE_PREFIX,
                QUOTES_HEADER,
                csvRecord -> ImmutableQuote.builder()
                        .ticker(ticker)
                        .sequenceNumber(Long.parseLong(csvRecord.get("sequenceNumber")))
                        .vendorTimestampNanos(Long.parseLong(csvRecord.get("vendorTimestampNanos")))
                        .exchangeTimestampNanos(Long.parseLong(csvRecord.get("exchangeTimestampNanos")))
                        .bidPrice(new BigDecimal(csvRecord.get("bidPrice")))
                        .bidSize(Long.parseLong(csvRecord.get("bidSize")))
                        .askPrice(new BigDecimal(csvRecord.get("askPrice")))
                        .askSize(Long.parseLong(csvRecord.get("askSize")))
                        .build()
        );
    }

    @Override
    public Stream<Trade> readTrades(String ticker, LocalDate valueDate) {
        return readEvents(
                ticker,
                valueDate,
                TRADES_FILE_PREFIX,
                TRADES_HEADER,
                csvRecord -> ImmutableTrade.builder()
                        .ticker(ticker)
                        .sequenceNumber(Long.parseLong(csvRecord.get("sequenceNumber")))
                        .vendorTimestampNanos(Long.parseLong(csvRecord.get("vendorTimestampNanos")))
                        .exchangeTimestampNanos(Long.parseLong(csvRecord.get("exchangeTimestampNanos")))
                        .price(new BigDecimal(csvRecord.get("price")))
                        .size(Long.parseLong(csvRecord.get("size")))
                        .build()
        );
    }

    private <T extends MarketDataEvent> Stream<T> readEvents(
            String ticker,
            LocalDate valueDate,
            String filePrefix,
            String[] csvHeader,
            Function<CSVRecord, T> eventExtractor
    ) {
        var directoryPath = Path.of(basePath, "market_data", valueDate.toString());
        var csvFileName = filePrefix + ticker + ".csv.gz";
        Path filePath = Path.of(directoryPath.toString(), csvFileName);

        try {
            var csvFormat = CSVFormat.Builder.create().setHeader(csvHeader).setSkipHeaderRecord(true)
                    .build();

            var gzipInputStream = new GZIPInputStream(new FileInputStream(filePath.toFile()));

            var csvParser = new CSVParser(new InputStreamReader(gzipInputStream), csvFormat);

            return csvParser.stream().map(eventExtractor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
