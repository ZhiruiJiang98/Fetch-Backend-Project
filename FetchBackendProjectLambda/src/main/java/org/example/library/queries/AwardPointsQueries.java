package org.example.library.queries;

public class AwardPointsQueries {
    public static String getAwardedPointsById(String id){
        return String.format("SELECT id, points FROM AwardedPoints WHERE id = '%s'", id);
    }
    public static String createAwardedPoints(String id, String points){
        return String.format("INSERT INTO AwardedPoints (id, points) VALUES ('%s', '%s')", id, points);
    }
}

