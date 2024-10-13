package org.example.library.queries;

import com.mysql.cj.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class SqlQueryBuilder {
    private static final Map<String, String> operatorMap = new HashMap<>();
    public SqlQueryBuilder() {
        operatorMap.put("==", "=");
        operatorMap.put("!=", "!=");
        operatorMap.put("<", "<");
        operatorMap.put("<=", "<=");
        operatorMap.put(">=", ">=");
        operatorMap.put(">", ">");
        operatorMap.put("=@", " LIKE ");
        operatorMap.put("!@", " NOT LIKE ");
    }

    public String build(String query,
                        String filters,
                        String sort,
                        String order,
                        int offset,
                        int limit) {
        StringBuilder queryBuilder = new StringBuilder(query);
        // Adding filters
        if (!StringUtils.isNullOrEmpty(filters)) {
            filters = filters
                    .replace("%27", "'")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace(";", " AND ")
                    .replace(",", " OR ");
            for (String key: operatorMap.keySet()) {
                filters = filters.replace(key, operatorMap.get(key));
            }

            queryBuilder.append(" WHERE ");
            queryBuilder.append(filters);

        }

        // Adding sort
        if (!StringUtils.isNullOrEmpty(sort)) {
            queryBuilder.append(" ORDER BY ");
            queryBuilder.append(sort);

            // Adding order
            if (!StringUtils.isNullOrEmpty(order)) {
                queryBuilder.append(" ");
                queryBuilder.append(order);
            }
        }

        // Adding limit
        if (limit != 0) {
            queryBuilder.append(" LIMIT ");
            queryBuilder.append(limit);
        }

        // Adding offset
        if (offset != 0) {
            queryBuilder.append(" OFFSET ");
            queryBuilder.append(offset);
        }

        return queryBuilder.toString();
    }

    public String buildNew(String query,
                           String filters,
                           String sort,
                           String order,
                           int offset,
                           int limit,
                           Map<String, String> keysToReplace) {
        StringBuilder queryBuilder = new StringBuilder(query);
        // Adding filters
        if (!StringUtils.isNullOrEmpty(filters)) {
            String[] conditions = filters.split(";");
            for (int i = 0; i < conditions.length; i++) {
                String condition = conditions[i];
                for (String operator: operatorMap.keySet()) {
                    if (condition.contains(operator)) {
                        String[] c = condition.split(operator);
                        String lhs = keysToReplace.containsKey(c[0]) ? keysToReplace.get(c[0]) : c[0];
                        String rhs = c[1].replace("%27", "'")
                                .replace("%28", "(")
                                .replace("%29", ")");
                        condition = lhs + operatorMap.get(operator) + rhs;
                    }
                }
                conditions[i] = condition;
            }

            queryBuilder.append(" WHERE ");
            queryBuilder.append(String.join(" AND ", conditions));
        }

        correctQueryOrder(queryBuilder);

        // Adding sort
        if (!StringUtils.isNullOrEmpty(sort)) {
            queryBuilder.append(" ORDER BY ");
            queryBuilder.append(keysToReplace.getOrDefault(sort, sort));

            // Adding order
            if (!StringUtils.isNullOrEmpty(order)) {
                queryBuilder.append(" ");
                queryBuilder.append(order);
            }
        }

        // Adding limit
        if (limit != 0) {
            queryBuilder.append(" LIMIT ");
            queryBuilder.append(limit);
        }

        // Adding offset
        if (offset != 0) {
            queryBuilder.append(" OFFSET ");
            queryBuilder.append(offset);
        }

        return queryBuilder.toString();
    }

    public static void correctQueryOrder(StringBuilder query) {
        // Find indices of GROUP BY and WHERE clauses
        int groupByIndex = query.indexOf("GROUP BY");
        int whereIndex = query.indexOf("WHERE");

        // Ensure both GROUP BY and WHERE exist, and WHERE comes after GROUP BY
        if (groupByIndex != -1 && whereIndex != -1 && whereIndex > groupByIndex) {
            // Extract the parts of the query
            String selectPart = query.substring(0, groupByIndex).trim(); // Before GROUP BY
            String groupByPart = query.substring(groupByIndex, whereIndex).trim(); // GROUP BY part
            String wherePart = query.substring(whereIndex).trim(); // WHERE and beyond

            // Clear the StringBuilder and rebuild the query in the correct order
            query.setLength(0); // Clear the original query
            query.append(selectPart).append(" ").append(wherePart).append(" ").append(groupByPart);
        }
    }
}
