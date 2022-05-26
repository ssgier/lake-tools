package io.github.ssgier.laketools.spiketrains.transformer;

import java.time.LocalDate;
import java.util.List;

public class TransformationSpec {

    public static class InputItem {
        private final String ticker;
        private final LocalDate valueDate;

        public InputItem(String ticker, LocalDate valueDate) {
            this.ticker = ticker;
            this.valueDate = valueDate;
        }

        public String getTicker() {
            return ticker;
        }

        public LocalDate getValueDate() {
            return valueDate;
        }
    }

    private final List<InputItem> inputItems;
    private final String targetTicker;

    public TransformationSpec(List<InputItem> inputItems, String targetTicker) {
        this.inputItems = inputItems;
        this.targetTicker = targetTicker;
    }

    public List<InputItem> getInputItems() {
        return inputItems;
    }

    public String getTargetTicker() {
        return targetTicker;
    }
}
