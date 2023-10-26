package org.galal.sqlrunner.services.verticles;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.galal.sqlrunner.services.database.ReactiveSqlDbClient;
import org.galal.sqlrunner.services.verticles.messages.SqlFilePathMsg;
import org.jboss.logging.Logger;

import static org.galal.sqlrunner.services.verticles.enums.Messages.EXECUTE_SQL;
import static org.galal.sqlrunner.services.verticles.enums.Messages.GET_FILE;

@ApplicationScoped
public class SqlExecuterVerticle {
    private static final Logger LOG = Logger.getLogger(SqlExecuterVerticle.class);

    @Inject
    EventBus bus;

    @Inject
    ReactiveSqlDbClient reactiveDbClient;

    @ConsumeEvent(value = EXECUTE_SQL, blocking = true)
    @RunOnVirtualThread
    public String executeSql(SqlFilePathMsg msg){
        try {
            var sql = bus.requestAndAwait(GET_FILE, msg.file()).body().toString();
            var result = reactiveDbClient.query(sql, msg.params());
            LOG.info("Executor called the query!");
            return result;
        } catch (Exception e) {
            LOG.error("Executor Failed!", e);
            throw new RuntimeException(e);
        }
    }
}
