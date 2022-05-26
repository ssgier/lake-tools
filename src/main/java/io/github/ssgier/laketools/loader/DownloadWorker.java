package io.github.ssgier.laketools.loader;

import java.util.Collection;

public class DownloadWorker {
    private final DownloadJobFactory downloadJobFactory;

    public DownloadWorker(DownloadJobFactory downloadJobFactory) {
        this.downloadJobFactory = downloadJobFactory;
    }

    public void process(Collection<DownloadJobSpecification> downloadJobSpecifications) {
        downloadJobSpecifications.stream()
                .parallel()
                .map(downloadJobFactory::createDownloadJob)
                .forEach(DownloadJob::run);
    }
}
