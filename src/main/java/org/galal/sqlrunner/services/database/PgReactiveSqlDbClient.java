package org.galal.sqlrunner.services.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.properties.IfBuildProperty;
import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
@IfBuildProperty(name = "quarkus.datasource.db-kind", stringValue = "postgresql")
public class PgReactiveSqlDbClient implements ReactiveSqlDbClient{
    private static final Logger LOG = Logger.getLogger(PgReactiveSqlDbClient.class);


    @Inject
    ObjectMapper objectMapper;

    @Inject
    PgPool client;


    @Override
    public String query(String sql, Map<String, ?> params) {
        LOG.info(format("Database client running query: [%s]",sql));

        var pgQuery = PgQueryModifier.toPgQuery(sql, params);

        return client.preparedQuery(pgQuery.query())
                            .mapping(this::rowAsKeyValue)
                            .execute(Tuple.from(pgQuery.params()))
                            .toMulti()
                            .flatMap(Multi.createFrom()::iterable)
                            .collect().asList()
                            .map(this::toJsonString)
                            .await().indefinitely();
    }

    private String toJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    record ColumnData(String columnName, Object value){}

    private Map<String,Object> rowAsKeyValue(Row row) {
        return IntStream.range(0, row.size())
                .mapToObj(i -> new ColumnData(row.getColumnName(i), row.getValue(i)))
                .collect(
                    toMap(ColumnData::columnName, ColumnData::value));
    }

    @Override
    public void execute(String sql, Map<String, ?> params) {
        LOG.info(format("Database client running query: [%s]",sql));

        var pgQuery = PgQueryModifier.toPgQuery(sql, params);

        client.preparedQuery(pgQuery.query())
                .executeAndAwait(Tuple.from(pgQuery.params()));
    }


    @Override
    public void execute(String sql) {
        LOG.info(format("Database client running query: [%s]",sql));

        Stream.of(sql.split(";"))
              .forEach(query -> client.query(query).executeAndAwait());
    }

}
