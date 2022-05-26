package io.github.ssgier.laketools.loader;

public class DownloadJobFactoryImpl implements DownloadJobFactory {

    private final QuotesLoader quotesLoader;
    private final TradesLoader tradesLoader;
    private final Persistor persistor;

    public DownloadJobFactoryImpl(QuotesLoader quotesLoader, TradesLoader tradesLoader, Persistor persistor) {
        this.quotesLoader = quotesLoader;
        this.tradesLoader = tradesLoader;
        this.persistor = persistor;
    }

    @Override
    public DownloadJob createDownloadJob(DownloadJobSpecification downloadJobSpecification) {
        return new DownloadJob(
                quotesLoader,
                tradesLoader,
                persistor,
                downloadJobSpecification.getTicker(),
                downloadJobSpecification.getValueDate());
    }
}
