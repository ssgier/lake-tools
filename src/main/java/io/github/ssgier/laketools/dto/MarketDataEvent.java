package io.github.ssgier.laketools.dto;

public interface MarketDataEvent {
    String ticker();

    long vendorTimestampNanos();

    long exchangeTimestampNanos();

    long sequenceNumber();
}
