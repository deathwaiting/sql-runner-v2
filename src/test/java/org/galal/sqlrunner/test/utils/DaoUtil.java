package org.galal.sqlrunner.test.utils;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.galal.sqlrunner.services.database.ReactiveSqlDbClient;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.result.ResultIterable;

import java.util.Map;

@ApplicationScoped
public class DaoUtil {

    @Inject
    ReactiveSqlDbClient dbClient;

    private <T> ResultIterable<? extends T> queryAndMapToBean(String sql, Class<? extends T> resultType, Map<String, ?> params, Handle h) {
        return h.createQuery(sql)
                .bindMap(params)
                .mapTo(resultType);
    }


    public void execute(String sql){
        dbClient.execute(sql);
    }
}
