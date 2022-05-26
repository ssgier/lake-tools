package io.github.ssgier.laketools.loader;

import java.time.LocalDate;

public class DownloadJobSpecification {
    private final String ticker;
    private final LocalDate valueDate;

    public DownloadJobSpecification(String ticker, LocalDate valueDate) {
        this.ticker = ticker;
        this.valueDate = valueDate;
    }

    public String getTicker() {
        return ticker;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }
}
