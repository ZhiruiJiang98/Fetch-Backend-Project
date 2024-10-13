package org.example.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysql.cj.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.library.model.constant.ActionResponseStatus;
import org.example.library.model.dto.AwardedPointsDto;
import org.example.library.model.dto.PointsDto;
import org.example.library.model.server.ActionResponse;
import org.example.library.mysql.MysqlClient;
import org.example.library.storage.FetchBackendProjectStorageManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class GetAwardedPointsAction implements AbstractAction<APIGatewayProxyRequestEvent, PointsDto> {
    private final FetchBackendProjectStorageManager manager;
    private final Logger LOGGER = LogManager.getLogger(GetAwardedPointsAction.class);
    private final String host;
    private final String user;
    private final String pwd;
    private final MysqlClient client;

    @Inject
    public GetAwardedPointsAction(FetchBackendProjectStorageManager manager,
                                  MysqlClient client,
                                  @Named("MysqlHost") String dbHost,
                                  @Named("MysqlUser") String dbUser,
                                  @Named("MysqlPassword") String dbPassword) {
        this.manager = manager;
        this.client = client;
        this.host = dbHost;
        this.user = dbUser;
        this.pwd = dbPassword;
    }

    @Override
    public String getActionName() {
        return "GetAwardedPointsAction";
    }

    @Override
    public ActionResponse<PointsDto> processRequest(APIGatewayProxyRequestEvent event) throws SQLException{
        LOGGER.info("Starting GetAwardedPointsAction");
        System.out.println("Starting GetAwardedPointsAction");
        if(!requestValidated(event)) {
            return constructResponse(
                    ActionResponseStatus.BAD_REQUEST,
                    "Request body does not have all the required fields...",
                    null,
                    getActionName()
            );
        }
        try {
            client.connect(this.host, this.user, this.pwd);
            String receiptId = event.getPathParameters().get("id");
            Optional<AwardedPointsDto> resultSets = manager.getAwardedPointsById(client, receiptId);
            client.close();

            if(!resultSets.isPresent()) {
                return constructResponse(
                        ActionResponseStatus.NOT_FOUND,
                        "No awarded points found for receipt id: " + receiptId,
                        null,
                        getActionName()
                );
            }
            AwardedPointsDto awardedPointsDto = resultSets.get();
            PointsDto points = PointsDto.builder()
                    .points(awardedPointsDto.getPoints())
                    .build();
            return constructResponse(
                    ActionResponseStatus.OK,
                    "Points found",
                    points,
                    getActionName()
            );

        }catch(SQLException ex){
            LOGGER.error("Error in GetAwardedPointsAction: " + ex.getMessage());
            return constructResponse(
                    ActionResponseStatus.INTERNAL_SERVER_ERROR,
                    "Error in GetAwardedPointsAction: " + ex.getMessage(),
                    null,
                    getActionName()
            );
        }
    }

    @Override
    public boolean requestValidated(APIGatewayProxyRequestEvent event) {
        Map<String, String> pathParamMap = event.getPathParameters();
        if (pathParamMap == null ) {
            return false;
        }
        return !pathParamMap.isEmpty() && pathParamMap.containsKey("id") && !StringUtils.isNullOrEmpty(pathParamMap.get("id"));
    }
}
