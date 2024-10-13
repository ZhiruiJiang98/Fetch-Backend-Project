package org.example.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysql.cj.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.library.model.constant.ActionResponseStatus;
import org.example.library.model.dto.ItemDto;
import org.example.library.model.dto.ReceiptDto;
import org.example.library.model.server.ActionResponse;
import org.example.library.mysql.MysqlClient;
import org.example.library.storage.FetchBackendProjectStorageManager;

import java.sql.SQLException;
import java.util.UUID;

public class CreateProcessReceiptsAction implements AbstractAction<APIGatewayProxyRequestEvent, String> {
    private final FetchBackendProjectStorageManager manager;
    private final Logger LOGGER = LogManager.getLogger(CreateProcessReceiptsAction.class);
    private final String host;
    private final String user;
    private final String pwd;
    private final MysqlClient client;

    @Inject
    public CreateProcessReceiptsAction(FetchBackendProjectStorageManager manager,
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
        return "CreateProcessReceiptsAction";
    }

    @Override
    public ActionResponse<String> processRequest(APIGatewayProxyRequestEvent event) throws SQLException {
        LOGGER.info("Starting CreateProcessReceiptsAction");
        System.out.println("Starting CreateProcessReceiptsAction");
        if (!requestValidated(event)) {
            return constructResponse(
                    ActionResponseStatus.BAD_REQUEST,
                    "Request body does not have all the required fields...",
                    null,
                    getActionName()
            );
        }
        try {
            this.client.connect(this.host, this.user, this.pwd);
            Gson gson = new Gson();
            ReceiptDto receipt = gson.fromJson(event.getBody(), ReceiptDto.class);
            String points = Integer.toString(calPoints(receipt));

            String receiptId = UUID.randomUUID().toString();
            manager.createAwardedPoints(client, receiptId, points);
            return constructResponse(
                    ActionResponseStatus.OK,
                    "Successfully processed receipt...",
                    receiptId,
                    getActionName()
            );

        } catch (SQLException ex) {
            LOGGER.error("SQLException: {}", ex.getMessage());
            return constructResponse(
                    ActionResponseStatus.INTERNAL_SERVER_ERROR,
                    "Error processing request...",
                    null,
                    getActionName()
            );
        } finally {
            this.client.close();
        }
    }

    private int calPoints(ReceiptDto receipt) {
        int points = 0;
        points += receipt.getRetailer().replaceAll("[^A-Za-z0-9]", "").length();
        double total = Double.parseDouble(receipt.getTotal());
        if (total == Math.floor(total)) {
            points += 50;
        }
        if (total % 0.25 == 0) {
            points += 25;
        }
        points += (receipt.getItems().size() / 2) * 5;
        for (ItemDto item : receipt.getItems()) {
            String description = item.getShortDescription().trim();
            if (description.length() % 3 == 0) {
                double price = Double.parseDouble(item.getPrice());
                points += (int) Math.ceil(price * 0.2);
            }
        }
        int day = Integer.parseInt(receipt.getPurchaseDate().split("-")[2]);
        if (day % 2 != 0) {
            points += 6;
        }
        String[] time = receipt.getPurchaseTime().split(":");
        int hour = Integer.parseInt(time[0]);
        if (hour == 14 || (hour == 15 && Integer.parseInt(time[1]) < 60)) {
            points += 10;
        }

        return points;
    }

    @Override
    public boolean requestValidated(APIGatewayProxyRequestEvent event) {
        try {
            if (event.getBody() == null || StringUtils.isNullOrEmpty(event.getBody())) {
                return false;
            }
            Gson gson = new Gson();
            ReceiptDto receipt = gson.fromJson(event.getBody(), ReceiptDto.class);
            return receipt.getItems() != null
                    && receipt.getTotal() != null
                    && receipt.getRetailer() != null;
        } catch (JsonSyntaxException e) {
            LOGGER.info("Json Syntax Exception: " + e.getMessage());
            return false;
        }
    }
}
