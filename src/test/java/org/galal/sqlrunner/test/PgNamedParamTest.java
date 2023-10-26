package org.galal.sqlrunner.test;

import org.galal.sqlrunner.services.database.PgQueryModifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PgNamedParamTest {
    private final String QUERY = """
            SELECT * FROM CATS
            WHERE id = :id AND cute_level = :level AND time = TIMESTAMP '2020-11-30 20:00:00Z'
            """;

    private final String EXPECTED = """
            SELECT * FROM CATS
            WHERE id = $1 AND cute_level = $2 AND time = TIMESTAMP '2020-11-30 20:00:00Z'
            """;

    @Test
    void testQueryModification() {
        var result = PgQueryModifier.toPgQuery(QUERY, Map.of("id", 1, "level", "very cute"));
        assertEquals(EXPECTED, result.query());
        assertEquals(List.of(1, "very cute"), List.of(result.params()));

    }

}