package io.github.ssgier.laketools.loader;

import io.github.ssgier.laketools.loader.creation.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private final static Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        logger.info("Application starting");
        var injector = new Injector();

        try {
            injector.getDownloadWorker().process(injector.getDownloadJobSpecsSupplier().get());
        } catch(Throwable t) {
            logger.error(t);
        } finally {
            logger.info("Done, terminating");
            System.exit(0);
        }
    }
}
