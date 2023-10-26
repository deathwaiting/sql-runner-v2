package org.galal.sqlrunner.services.database;

import java.util.Map;

public interface ReactiveSqlDbClient {
    String query(String sql, Map<String,?> params);
    void execute(String sql, Map<String,?> params);

    void execute(String sql);
}
