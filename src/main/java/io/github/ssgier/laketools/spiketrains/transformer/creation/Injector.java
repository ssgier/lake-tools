package io.github.ssgier.laketools.spiketrains.transformer.creation;

import io.github.ssgier.laketools.persistor.Persistor;
import io.github.ssgier.laketools.persistor.PersistorImpl;
import io.github.ssgier.laketools.spiketrains.transformer.*;
import io.github.ssgier.laketools.spiketrains.transformer.event.RelativeTimeMapper;
import io.github.ssgier.laketools.spiketrains.transformer.reader.ReaderImpl;
import io.github.ssgier.laketools.util.BasePathProvider;

import java.util.function.Supplier;

public class Injector {

    private final static long startTimestampNanos = (long)1.5852293921032686e18;

    private final Supplier<TransformationSpec> transformationSpecSupplier;
    private final Transformer transformer;
    private final Persistor persistor;
    private final Repeater repeater;

    public Injector() {
        var timeMapper = new RelativeTimeMapper(startTimestampNanos, 1L);
        transformationSpecSupplier = new TransformationSpecSupplier();
        var reader = new ReaderImpl(BasePathProvider.getBasePath());
        var marketDataAggregator = new MarketDataAggregatorImpl(reader);
        var spikeTrainTransformer = new SpikeTrainTransformerImpl(marketDataAggregator, timeMapper);
        transformer = new TransformerImpl(marketDataAggregator, spikeTrainTransformer, timeMapper);
        persistor = new PersistorImpl(BasePathProvider.getBasePath());
        repeater = new RepeaterImpl();
    }

    public Supplier<TransformationSpec> getTransformationSpecSupplier() {
        return transformationSpecSupplier;
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public Persistor getPersistor() {
        return persistor;
    }

    public Repeater getRepeater() {
        return repeater;
    }
}
