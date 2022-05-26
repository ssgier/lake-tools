package io.github.ssgier.laketools.config;

public class Config {
    public final static String[] QUOTES_HEADER = {
            "sequenceNumber",
            "vendorTimestampNanos",
            "exchangeTimestampNanos",
            "bidPrice",
            "bidSize",
            "askPrice",
            "askSize"
    };

    public final static String QUOTES_FILE_PREFIX = "quotes_";

    public final static String[] TRADES_HEADER = {
            "sequenceNumber",
            "vendorTimestampNanos",
            "exchangeTimestampNanos",
            "price",
            "size"
    };

    public final static String TRADES_FILE_PREFIX = "trades_";
}
