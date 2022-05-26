package io.github.ssgier.laketools.loader;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PolygonDownloadSpecsSupplier implements Supplier<List<DownloadJobSpecification>> {

    @Override
    public List<DownloadJobSpecification> get() {

        var valueDate = LocalDate.of(2020, 3, 26);

                var tickers = List.of(
                "AAPL",
                "NVDA",
                "TSLA",
                "GOOG",
                "MSFT",
                "ACN",
                "AAP",
                "ABT",
                "ADM",
                "PYPL",
                "CMI"
        );

        return tickers
                .stream()
                .limit(50)
                .map(ticker_ -> new DownloadJobSpecification(ticker_, valueDate)).collect(Collectors.toList());
    }
}
