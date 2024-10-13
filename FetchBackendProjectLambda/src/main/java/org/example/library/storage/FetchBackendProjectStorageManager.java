package org.example.library.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.library.model.dto.AwardedPointsDto;
import org.example.library.mysql.MysqlClient;
import org.example.library.queries.AwardPointsQueries;
import org.example.library.queries.SqlQueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FetchBackendProjectStorageManager {
    private final SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
    private final Logger LOGGER = LogManager.getLogger(FetchBackendProjectStorageManager.class);
    public FetchBackendProjectStorageManager(){

    }
    public Optional<AwardedPointsDto> getAwardedPointsById(MysqlClient client, String id) throws SQLException {
        LOGGER.info(String.format("Getting awarded points with id: %s", id));
        System.out.println(String.format("Getting awarded points with id: %s", id));
        try {
            String query = AwardPointsQueries.getAwardedPointsById(id);
            LOGGER.info("Executing getAwardedPointsById query: " + query);
            System.out.println("Executing getAwardedPointsById query: " + query);
            ResultSet rs = client.executeQuery(query);
            List<AwardedPointsDto> awardedPoints = new ArrayList<>();

            while(rs.next()){
                AwardedPointsDto awardedPointsDto = AwardedPointsDto.builder()
                        .id(rs.getString("id"))
                        .points(rs.getString("points"))
                        .build();
                awardedPoints.add(awardedPointsDto);
            }
            System.out.println("AwardedPoint's size: " + awardedPoints.size());
            return awardedPoints.isEmpty() ? Optional.empty() : Optional.of(awardedPoints.get(0));
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }
    public String createAwardedPoints(MysqlClient client, String id, String points){
        LOGGER.info(String.format("Creating awarded point with id: %s", id));
        try{
            int rs = client.executeUpdate(AwardPointsQueries.createAwardedPoints(id, points));
            return rs == 1 ? id : null;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return "Error";
        }

    }
}
