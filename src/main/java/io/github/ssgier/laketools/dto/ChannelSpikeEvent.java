package io.github.ssgier.laketools.dto;

import org.immutables.value.Value;

@Value.Immutable
public interface ChannelSpikeEvent {
    double time();
    long channelId();
}
