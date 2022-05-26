package io.github.ssgier.laketools.spiketrains.transformer.event;

public class RelativeTimeMapper implements TimeMapper {

    private final long offsetTimestampNanos;
    private final double scaleFactor;

    public RelativeTimeMapper(long offsetTimestampNanos, double scaleFactor) {
        this.offsetTimestampNanos = offsetTimestampNanos;
        this.scaleFactor = scaleFactor;
    }

    @Override
    public double toTime(long timestampNanos) {
        return scaleFactor * (timestampNanos - offsetTimestampNanos) * 1e-9;
    }
}
