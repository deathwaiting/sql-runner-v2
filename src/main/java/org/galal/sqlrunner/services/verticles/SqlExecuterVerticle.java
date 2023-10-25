package org.galal.sqlrunner.services.verticles;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.galal.sqlrunner.services.database.ReactiveSqlDbClient;
import org.galal.sqlrunner.services.verticles.messages.SqlFilePathMsg;
import org.jboss.logging.Logger;

import java.util.Map;

import static java.util.Optional.ofNullable;
import static org.galal.sqlrunner.services.verticles.enums.Messages.EXECUTE_SQL;
import static org.galal.sqlrunner.services.verticles.enums.Messages.GET_FILE;

@ApplicationScoped
public class SqlExecuterVerticle {
    private static final Logger LOG = Logger.getLogger(SqlExecuterVerticle.class);

    @Inject
    EventBus bus;

    @Inject
    ReactiveSqlDbClient reactiveDbClient;

    @ConsumeEvent(EXECUTE_SQL)
    public Uni<String> executeSql(SqlFilePathMsg msg){
        return bus
                .request(GET_FILE, msg.file())
                .flatMap(sqlScriptMsg -> executeSql(sqlScriptMsg, msg.params()));
    }



    private Uni<String> executeSql(Message<?> sqlScriptMsg, Map<String,String> paramsAsMap){
       var sql = (String) sqlScriptMsg.body();
       return reactiveDbClient
               .query(sql, paramsAsMap)
               .onItem()
               .invoke(res -> LOG.info("Executor called the query!"))
               .onFailure()
               .invoke(e -> LOG.error("Executor Failed!", e));
    }


    private String getValue(Map.Entry<String,Object> entry){
        return ofNullable(entry)
                .map(Map.Entry::getValue)
                .map(Object::toString)
                .orElse(null);
    }
}
