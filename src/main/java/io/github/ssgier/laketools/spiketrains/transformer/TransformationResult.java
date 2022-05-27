package io.github.ssgier.laketools.spiketrains.transformer;

import io.github.ssgier.laketools.spiketrains.transformer.dto.ChannelSpikeEvent;
import io.github.ssgier.laketools.spiketrains.transformer.dto.PriceEvent;

import java.util.List;

public class TransformationResult {
    private final TransformationSpec transformationSpec;
    private final List<ChannelSpikeEvent> channelSpikeEvents;
    private final List<PriceEvent> targetTickerPriceEvents;

    public TransformationResult(TransformationSpec transformationSpec, List<ChannelSpikeEvent> channelSpikeEvents, List<PriceEvent> targetTickerPriceEvents) {
        this.transformationSpec = transformationSpec;
        this.channelSpikeEvents = channelSpikeEvents;
        this.targetTickerPriceEvents = targetTickerPriceEvents;
    }

    public TransformationSpec getTransformationSpec() {
        return transformationSpec;
    }

    public List<ChannelSpikeEvent> getChannelSpikeEvents() {
        return channelSpikeEvents;
    }

    public List<PriceEvent> getTargetTickerPriceEvents() {
        return targetTickerPriceEvents;
    }
}
