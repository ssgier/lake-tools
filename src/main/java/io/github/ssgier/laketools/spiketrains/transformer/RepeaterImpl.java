package io.github.ssgier.laketools.spiketrains.transformer;

import io.github.ssgier.laketools.spiketrains.transformer.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class RepeaterImpl implements Repeater {

    @Override
    public TransformationResult repeat(
            TransformationResult transformationResult,
            int numRepetitions,
            double fromTime,
            double toTime,
            double randomTimeGapFromTime,
            double randomTimeGapToTime) {


        List<ChannelSpikeEvent> spikeEventsResult = new ArrayList<>();
        List<PriceEvent> priceEventsResult = new ArrayList<>();

        var random = new Random(0);
        var baseTime = 0;
        var windowTime = toTime - fromTime;

        for (int i = 0; i < numRepetitions; ++i) {

            var windowEndTime = baseTime + windowTime;

            rebase(
                    transformationResult.getChannelSpikeEvents(),
                    fromTime,
                    toTime,
                    baseTime,
                    RepeaterImpl::remapTime).forEach(spikeEventsResult::add);

            rebase(
                    transformationResult.getTargetTickerPriceEvents(),
                    fromTime,
                    toTime,
                    baseTime,
                    RepeaterImpl::remapTime).forEach(priceEventsResult::add);

            var lastPriceEvent = priceEventsResult.get(priceEventsResult.size() - 1);
            priceEventsResult.add(ImmutablePriceEvent.builder()
                    .from(lastPriceEvent)
                    .bidSize(0)
                    .askSize(0)
                    .time(windowEndTime)
                    .build());

            baseTime += windowTime + randomTimeGapFromTime + random.nextDouble() * (randomTimeGapToTime - randomTimeGapFromTime);
        }

        return new TransformationResult(
                transformationResult.getTransformationSpec(),
                spikeEventsResult,
                priceEventsResult);
    }

    private <T extends TimedEvent> Stream<T> rebase(
            List<T> events,
            double fromTime,
            double toTime,
            double baseTime,
            BiFunction<T, Double, T> timeRemapping
            ) {
        return events.stream()
                .filter(event -> event.time() >= fromTime)
                .filter(event -> event.time() < toTime)
                .map(event -> timeRemapping.apply(event, baseTime + event.time() - fromTime));
    }

    private static PriceEvent remapTime(PriceEvent priceEvent, double time) {
        return ImmutablePriceEvent.builder().from(priceEvent).time(time).build();
    }

    private static ChannelSpikeEvent remapTime(ChannelSpikeEvent spikeEvent, double time) {
        return ImmutableChannelSpikeEvent.builder().from(spikeEvent).time(time).build();
    }
}
