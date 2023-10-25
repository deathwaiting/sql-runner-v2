package org.galal.sqlrunner.services.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.properties.IfBuildProperty;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@ApplicationScoped
@IfBuildProperty(name = "quarkus.datasource.db-kind", stringValue = "postgresql")
public class PgReactiveSqlDbClient implements ReactiveSqlDbClient{
    private static final Logger LOG = Logger.getLogger(ReactiveSqlDbClient.class);


    @Inject
    ObjectMapper objectMapper;

    @Inject
    PgPool client;


    @Override
    public Uni<String> query(String sql, Map<String, String> params) {
        LOG.info(format("Database client running query: [%s]",sql));

        var pgQuery = PgQueryModifier.toPgQuery(sql, params);

        return client.preparedQuery(pgQuery.query())
                .execute(Tuple.from(pgQuery.params()))
                .map(this::toMap)
                .map(this::toJsonString);
    }

    private String toJsonString(final Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String,Object> toMap(RowSet<Row> rows) {
        var result = new HashMap<String,Object>();
        for(var row: rows) {
            rows.columnsNames()
                    .forEach(col -> result.put(col, row.getValue(col)));
        }
        return result;
    }

    @Override
    public Uni<Integer> execute(String sql, Map<String, String> params) {
        return null;
    }

}
