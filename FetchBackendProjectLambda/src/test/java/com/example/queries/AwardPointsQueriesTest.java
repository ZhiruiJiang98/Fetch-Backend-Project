package com.example.queries;

import org.example.library.queries.AwardPointsQueries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AwardPointsQueriesTest {
    private static final String TEST_ID = "testId";
    private static final String TEST_POINTS = "testPoints";
    private static final String EXPECTED_GET_AWARDED_POINTS_BY_ID = "SELECT id, points FROM AwardedPoints WHERE id = 'testId'";
    private static final String EXPECTED_CREATE_AWARDED_POINTS = "INSERT INTO AwardedPoints (id, points) VALUES ('testId', 'testPoints')";
    @Test
    public void test_get_awarded_points_by_id() {
        assertEquals(EXPECTED_GET_AWARDED_POINTS_BY_ID, AwardPointsQueries.getAwardedPointsById(TEST_ID));
    }

    @Test
    public void test_create_awarded_points() {
        assertEquals(EXPECTED_CREATE_AWARDED_POINTS, AwardPointsQueries.createAwardedPoints(TEST_ID, TEST_POINTS));
    }
}
