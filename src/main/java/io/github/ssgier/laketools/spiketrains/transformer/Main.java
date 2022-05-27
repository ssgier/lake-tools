package io.github.ssgier.laketools.spiketrains.transformer;

import io.github.ssgier.laketools.spiketrains.transformer.creation.Injector;

public class Main {
    public static void main(String[] args) {
        var injector = new Injector();

        var result = injector.getTransformer()
                .process(injector.getTransformationSpecSupplier().get());

        var repeatedResult = injector.getRepeater().repeat(
                result,
                30,
                100, 200,
                0.5, 5);

        injector.getPersistor().persistTransformationResult(repeatedResult);
    }
}
