package io.github.ssgier.laketools.spiketrains.transformer;

import io.github.ssgier.laketools.spiketrains.transformer.creation.Injector;

public class Main {
    public static void main(String[] args) {
        var injector = new Injector();

        injector.getPersistor().persistTransformationResult(
                injector.getTransformer().process(injector.getTransformationSpecSupplier().get())
        );
    }
}
