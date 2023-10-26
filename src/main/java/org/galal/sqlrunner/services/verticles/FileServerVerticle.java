package org.galal.sqlrunner.services.verticles;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.galal.sqlrunner.services.verticles.uitls.FileReader;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.lang.String.format;
import static java.nio.file.Files.notExists;
import static org.galal.sqlrunner.services.verticles.enums.Messages.GET_FILE;

@ApplicationScoped
public class FileServerVerticle {

    private static final Logger LOG = Logger.getLogger(FileServerVerticle.class);

    @Inject
    FileReader fileReader;


    @ConfigProperty(name = "org.galal.sql_runner.directory", defaultValue = "sql")
    String directoryPath;



    private final Path workingDir = FileSystems.getDefault().getPath(".").toAbsolutePath();


    public void init(@Observes StartupEvent e) {
        initializeSqlDirectory();
    }


    @ConsumeEvent(GET_FILE)
    @RunOnVirtualThread
    public String readFile(String filePathMessage){
         var path = Path.of(directoryPath).resolve(filePathMessage);
         return fileReader.readFileFromPath(path.toString());
    }


    private void initializeSqlDirectory(){
        Path path =
                Optional
                    .of(directoryPath)
                    .map(this::createSqlDirectoryPath)
                    .orElse(workingDir.resolve("sql"));
        if(notExists(path)){
            try {
                Files.createDirectory(path);
                LOG.info(format("Created SQL directory[%s]", path.toString()));
            } catch (IOException e) {
                LOG.error(format("Failed to create sql files directory[%s] ...", path.toString()), e);
            }
        }
        LOG.info(format("Using SQL directory[%s] ...", path.toString()));
    }


    private Path createSqlDirectoryPath(String pathString){
        return pathString.startsWith("/")? Path.of(pathString).toAbsolutePath() : workingDir.resolve(pathString);
    }
}

