package io.github.ssgier.laketools.spiketrains.transformer;

import io.github.ssgier.laketools.spiketrains.transformer.dto.ChannelSpikeEvent;

import java.util.List;

public interface SpikeTrainTransformer {
    List<ChannelSpikeEvent> transform(TransformationSpec transformationSpec);
}
