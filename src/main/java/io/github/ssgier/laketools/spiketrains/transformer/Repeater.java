package io.github.ssgier.laketools.spiketrains.transformer;

public interface Repeater {
    TransformationResult repeat(
            TransformationResult transformationResult,
            int numRepetitions,
            double fromTime,
            double toTime,
            double randomTimeGapFromTime,
            double randomTimeGapToTime);
}
