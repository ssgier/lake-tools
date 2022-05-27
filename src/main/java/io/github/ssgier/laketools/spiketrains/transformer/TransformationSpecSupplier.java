package io.github.ssgier.laketools.spiketrains.transformer;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

public class TransformationSpecSupplier implements Supplier<TransformationSpec> {
    @Override
    public TransformationSpec get() {

        var valueDate = LocalDate.of(2020, 3, 26);
        var ticker = "CMI";

        return new TransformationSpec(List.of(new TransformationSpec.InputItem(ticker, valueDate)), ticker);
    }
}
