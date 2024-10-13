package com.example.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.example.action.GetAwardedPointsAction;
import org.example.library.model.constant.ActionResponseStatus;
import org.example.library.model.dto.AwardedPointsDto;
import org.example.library.model.dto.PointsDto;
import org.example.library.model.server.ActionResponse;
import org.example.library.mysql.MysqlClient;
import org.example.library.storage.FetchBackendProjectStorageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class GetAwardedPointsActionTest {
    private static final String TEST_ID = "testId";
    private static final String TEST_POINTS = "100";
    private static final String TEST_DB_HOST = "localhost";
    private static final String TEST_DB_USER = "user";
    private static final String TEST_DB_PWD = "password";

    @InjectMocks
    private GetAwardedPointsAction action;

    @Mock
    private FetchBackendProjectStorageManager manager;

    @Mock
    private MysqlClient client;

    private APIGatewayProxyRequestEvent requestEvent;

    @BeforeEach
    public void setup() throws SQLException {
        MockitoAnnotations.openMocks(this);
        requestEvent = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", TEST_ID);
        requestEvent.setPathParameters(pathParams);

        when(manager.getAwardedPointsById(any(), anyString())).thenReturn(Optional.of(
                AwardedPointsDto.builder().id(TEST_ID).points(TEST_POINTS).build()));
        doNothing().when(client).connect(anyString(), anyString(), anyString());
        doNothing().when(client).close();
    }

    @Test
    public void testProcessRequest_Success() throws SQLException, IOException, NoSuchFieldException, IllegalAccessException {

        ActionResponse<PointsDto> response = action.processRequest(requestEvent);

        assertNotNull(response);
        assertEquals(ActionResponseStatus.OK, response.getActionResponseStatus());
        assertEquals(TEST_POINTS, response.getData().getPoints());
    }

    @Test
    public void testProcessRequest_AwardedPointsNotFound() throws SQLException, IOException, NoSuchFieldException, IllegalAccessException {

        when(manager.getAwardedPointsById(any(), anyString())).thenReturn(Optional.empty());

        ActionResponse<PointsDto> response = action.processRequest(requestEvent);

        assertNotNull(response);
        assertEquals(ActionResponseStatus.NOT_FOUND, response.getActionResponseStatus());
        assertNull(response.getData());
    }
    @Test
    public void testProcessRequest_InternalServerError() throws SQLException, IOException, NoSuchFieldException, IllegalAccessException {

        when(manager.getAwardedPointsById(any(), anyString())).thenThrow(new SQLException("Database error"));

        ActionResponse<PointsDto> response = action.processRequest(requestEvent);

        assertNotNull(response);
        assertEquals(ActionResponseStatus.INTERNAL_SERVER_ERROR, response.getActionResponseStatus());
        assertNull(response.getData());
    }
    @Test
    public void testRequestValidated_True() {
        boolean result = action.requestValidated(requestEvent);
        assertTrue(result);
    }

    @Test
    public void testRequestValidated_False_NoPathParameters() {
        requestEvent.setPathParameters(null);
        boolean result = action.requestValidated(requestEvent);
        assertFalse(result);
    }

    @Test
    public void testRequestValidated_False_EmptyId() {
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", "");
        requestEvent.setPathParameters(pathParams);
        boolean result = action.requestValidated(requestEvent);
        assertFalse(result);
    }

}
