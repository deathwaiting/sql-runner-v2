package org.galal.sqlrunner.services.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;

public class PgQueryModifier {
    public record NamedParamQuery(String query, Object... params) {}

    private record NamedParam(int startPosition, int endPosition, String name) {}

    /**
     * Process a query with named parameters (with [:] prefix, like oracle SQL), and convert it to use PostgreSql ordered parameters syntax.
     * */
    public static NamedParamQuery toPgQuery(String query, Map<String,?> params) {
        var modifiedQuery = query;
        var values = new ArrayList<>();
        var counter = new AtomicInteger(0);
        for(var param: extractNamedParams(query)) {
            var value = ofNullable(params.get(param.name()))
                            .orElseThrow(() -> new IllegalStateException("Missing value for paramter: " + param.name()));
            values.add(value);
            var index = counter.incrementAndGet();
            modifiedQuery = modifiedQuery.replace(":" + param.name(), "$" + index);
        }

        return new NamedParamQuery(modifiedQuery, values.toArray(new Object[]{}));
    }


    private static List<NamedParam> extractNamedParams(String query) {
        var withNoLaterals = query.replaceAll("('.+')", "");
        var pattern = Pattern.compile("(?<!')(:[A-Za-z]+\\w+)(?!')");
        var matcher = pattern.matcher(withNoLaterals.toLowerCase());
        List<NamedParam> params = new ArrayList<>();
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String part = matcher.group();
            params.add(new NamedParam(start, end, part.substring(1)));
        }
        return params;
    }

}