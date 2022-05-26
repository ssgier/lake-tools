package io.github.ssgier.laketools.spiketrains.transformer.event;

public interface TimeMapper {
    double toTime(long timestampNanos);
}
