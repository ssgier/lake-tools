package io.github.ssgier.laketools.loader.creation;

import io.github.ssgier.laketools.loader.*;
import io.github.ssgier.laketools.loader.polygon.PolygonApiKeyProvider;
import io.github.ssgier.laketools.loader.polygon.PolygonQuotesLoader;
import io.github.ssgier.laketools.loader.polygon.PolygonTradesLoader;
import io.github.ssgier.laketools.util.BasePathProvider;

import java.util.List;
import java.util.function.Supplier;

public class Injector {

    private final Supplier<List<DownloadJobSpecification>> downloadJobSpecsSupplier;
    private final DownloadWorker downloadWorker;

    public Injector() {
        downloadJobSpecsSupplier = new PolygonDownloadSpecsSupplier();

        var apiKey = PolygonApiKeyProvider.getApiKey();
        var quotesLoader = new PolygonQuotesLoader(apiKey);
        var tradesLoader = new PolygonTradesLoader(apiKey);
        var persistor = new PersistorImpl(BasePathProvider.getBasePath());

        downloadWorker = new DownloadWorker(new DownloadJobFactoryImpl(quotesLoader, tradesLoader, persistor));
    }

    public Supplier<List<DownloadJobSpecification>> getDownloadJobSpecsSupplier() {
        return downloadJobSpecsSupplier;
    }

    public DownloadWorker getDownloadWorker() {
        return downloadWorker;
    }
}
