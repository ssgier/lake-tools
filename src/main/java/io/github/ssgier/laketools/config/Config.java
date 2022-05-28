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

    public final static String[] CHANNEL_SPIKES_HEADER = {
            "time",
            "channelId"
    };

    public final static String CHANNEL_SPIKES_FILE_NAME = "channel_spikes";

    public final static String[] PRICES_HEADER = {
            "time",
            "bidPrice",
            "bidSize",
            "askPrice",
            "askSize"
    };

    public final static String PRICES_FILE_NAME = "prices";
}
