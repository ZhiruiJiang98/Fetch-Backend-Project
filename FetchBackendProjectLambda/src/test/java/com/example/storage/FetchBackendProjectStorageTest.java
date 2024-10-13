package com.example.storage;

import org.example.library.model.dto.AwardedPointsDto;
import org.example.library.mysql.MysqlClient;
import org.example.library.storage.FetchBackendProjectStorageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class FetchBackendProjectStorageTest {

    private static final String TEST_ID = "testId";
    private static final String TEST_POINTS = "100";

    private static final Optional<AwardedPointsDto> EXPECTED_RESULT = Optional.of(
            AwardedPointsDto.builder()
                    .id(TEST_ID)
                    .points(TEST_POINTS)
                    .build()
    );

    @InjectMocks
    private FetchBackendProjectStorageManager manager;

    @Mock
    private MysqlClient client;

    @Mock
    private ResultSet rs;

    @BeforeEach
    public void setup() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(client.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getString("id")).thenReturn(TEST_ID);
        when(rs.getString("points")).thenReturn(TEST_POINTS);
    }

    @Test
    public void testGetAwardedPointsById_Success() throws SQLException {

        Optional<AwardedPointsDto> actual = manager.getAwardedPointsById(client, TEST_ID);

        assertEquals(EXPECTED_RESULT, actual);
    }

    @Test
    public void testGetAwardedPointsById_EmptyResult() throws SQLException {
        when(rs.next()).thenReturn(false);

        Optional<AwardedPointsDto> actual = manager.getAwardedPointsById(client, TEST_ID);
        assertEquals(false,actual.isPresent());
    }

    @Test
    public void testCreateAwardedPoints_Success() throws SQLException {
        // Arrange
        when(client.executeUpdate(anyString())).thenReturn(1);

        // Act
        String result = manager.createAwardedPoints(client, TEST_ID, TEST_POINTS);

        // Assert
        assertEquals(TEST_ID, result);
    }

    @Test
    public void testCreateAwardedPoints_Failure() throws SQLException {
        // Arrange
        when(client.executeUpdate(anyString())).thenReturn(0);

        // Act
        String result = manager.createAwardedPoints(client, TEST_ID, TEST_POINTS);

        // Assert
        assertNull(result);
    }

    @Test
    public void testCreateAwardedPoints_Exception() throws SQLException {
        // Arrange
        when(client.executeUpdate(anyString())).thenThrow(new SQLException("Database error"));

        // Act
        String result = manager.createAwardedPoints(client, TEST_ID, TEST_POINTS);

        // Assert
        assertEquals("Error", result);
    }

}
