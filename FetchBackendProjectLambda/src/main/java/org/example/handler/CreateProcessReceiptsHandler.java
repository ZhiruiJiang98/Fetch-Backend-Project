package org.example.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.action.CreateProcessReceiptsAction;
import org.example.module.FetchBackendProjectModule;

import java.sql.SQLException;

public class CreateProcessReceiptsHandler implements AbstractHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOGGER = LogManager.getLogger(CreateProcessReceiptsHandler.class);
    private static final CreateProcessReceiptsAction createProcessReceiptsAction =
            Guice.createInjector(new FetchBackendProjectModule()).getInstance(CreateProcessReceiptsAction.class);
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LambdaLogger logger = context.getLogger();
        logger.log("Request Context: " + gson.toJson(context));
        logger.log("Request Event: " + gson.toJson(event));
        try{
            System.out.println("handler start ......");
            return processActionResponse(LOGGER, createProcessReceiptsAction.processRequest(event));
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
}
