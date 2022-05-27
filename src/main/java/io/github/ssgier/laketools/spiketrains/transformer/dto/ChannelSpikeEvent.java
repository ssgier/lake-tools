package io.github.ssgier.laketools.spiketrains.transformer.dto;

import org.immutables.value.Value;

@Value.Immutable
public interface ChannelSpikeEvent {
    double time();
    long channelId();
}
