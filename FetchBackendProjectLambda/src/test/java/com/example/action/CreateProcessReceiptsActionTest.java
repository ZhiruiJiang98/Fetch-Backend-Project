package com.example.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.example.action.CreateProcessReceiptsAction;
import org.example.library.model.constant.ActionResponseStatus;
import org.example.library.model.server.ActionResponse;
import org.example.library.mysql.MysqlClient;
import org.example.library.storage.FetchBackendProjectStorageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class CreateProcessReceiptsActionTest {

    private static final String TEST_RECEIPT_BODY = "{ \"retailer\": \"Target\", \"total\": \"35.00\", \"purchaseDate\": \"2024-05-10\", \"purchaseTime\": \"14:30\", \"items\": [ { \"shortDescription\": \"Milk\", \"price\": \"3.00\" } ] }";

    @InjectMocks
    private CreateProcessReceiptsAction action;

    @Mock
    private FetchBackendProjectStorageManager manager;

    @Mock
    private MysqlClient client;

    private APIGatewayProxyRequestEvent requestEvent;

    @BeforeEach
    public void setup() throws SQLException {
        MockitoAnnotations.initMocks(this);

        requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(TEST_RECEIPT_BODY);

        doNothing().when(client).connect(anyString(), anyString(), anyString());
        doNothing().when(client).close();
        when(manager.createAwardedPoints(any(MysqlClient.class), anyString(), anyString())).thenReturn(UUID.randomUUID().toString());
    }

    @Test
    public void testProcessRequest_Success() throws SQLException {
        ActionResponse<String> response = action.processRequest(requestEvent);
        assertEquals(ActionResponseStatus.OK, response.getActionResponseStatus());

    }

    @Test
    public void testProcessRequest_ValidationFailure() throws SQLException {
        requestEvent.setBody("{\"total\":\"35.00\"}");
        ActionResponse<String> response = action.processRequest(requestEvent);
        assertEquals(ActionResponseStatus.BAD_REQUEST, response.getActionResponseStatus());
    }

    @Test
    public void testProcessRequest_InvalidJson() throws SQLException {
        requestEvent.setBody("invalid json");
        ActionResponse<String> response = action.processRequest(requestEvent);
        assertEquals(ActionResponseStatus.BAD_REQUEST, response.getActionResponseStatus());
    }


    @Test
    public void testRequestValidated_True() {
        boolean isValid = action.requestValidated(requestEvent);

        assertTrue(isValid);
    }

    @Test
    public void testRequestValidated_False_EmptyBody() {
        requestEvent.setBody("");
        boolean isValid = action.requestValidated(requestEvent);
        assertFalse(isValid);
    }

    @Test
    public void testRequestValidated_False_MissingFields() {
        requestEvent.setBody("{\"total\":\"35.00\"}"); // Missing other required fields
        boolean isValid = action.requestValidated(requestEvent);
        assertFalse(isValid);
    }
}
