package io.github.ssgier.laketools.spiketrains.transformer;

public interface Transformer {
    TransformationResult process(TransformationSpec transformationSpec);
}
