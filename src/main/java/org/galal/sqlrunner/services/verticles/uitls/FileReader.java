package org.galal.sqlrunner.services.verticles.uitls;

import io.quarkus.cache.CacheResult;
import io.vertx.mutiny.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;


@ApplicationScoped
public class FileReader {

    private static final Logger LOG = Logger.getLogger(FileReader.class);

    @Inject
    Vertx vertx;

    @CacheResult(cacheName = "FILE_CONTENT")
    public String readFileFromPath(String path){
        LOG.info(String.format("reading contents of file[%s]", path));
        return vertx
                .fileSystem()
                .readFileAndAwait(path)
                .toString();
    }
}
