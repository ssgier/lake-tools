package io.github.ssgier.laketools.loader;

public interface DownloadJobFactory {
    DownloadJob createDownloadJob(DownloadJobSpecification downloadJobSpecification);
}
