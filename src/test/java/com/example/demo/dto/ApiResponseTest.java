package com.example.demo.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void constructor_ShouldCreateApiResponse() {
        // ACT
        ApiResponse<String> response = new ApiResponse<>("Success", "data");

        // ASSERT
        assertEquals("Success", response.getMessage());
        assertEquals("data", response.getData());
    }

    @Test
    void success_WithDataAndMessage_ShouldCreateSuccessResponse() {
        // ACT
        ApiResponse<String> response = ApiResponse.success("test data", "Operation successful");

        // ASSERT
        assertNotNull(response);
        assertEquals("Operation successful", response.getMessage());
        assertEquals("test data", response.getData());
    }

    @Test
    void success_WithMessageOnly_ShouldCreateSuccessResponseWithNullData() {
        // ACT
        ApiResponse<String> response = ApiResponse.success("Operation successful");

        // ASSERT
        assertNotNull(response);
        assertEquals("Operation successful", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void error_ShouldCreateErrorResponseWithNullData() {
        // ACT
        ApiResponse<String> response = ApiResponse.error("An error occurred");

        // ASSERT
        assertNotNull(response);
        assertEquals("An error occurred", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // ARRANGE
        ApiResponse<Integer> response = new ApiResponse<>();

        // ACT
        response.setMessage("Test message");
        response.setData(42);

        // ASSERT
        assertEquals("Test message", response.getMessage());
        assertEquals(42, response.getData());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // ARRANGE
        ApiResponse<String> response1 = new ApiResponse<>("msg", "data");
        ApiResponse<String> response2 = new ApiResponse<>("msg", "data");
        ApiResponse<String> response3 = new ApiResponse<>("different", "data");

        // ASSERT
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
    }

    @Test
    void hashCode_ShouldWorkCorrectly() {
        // ARRANGE
        ApiResponse<String> response1 = new ApiResponse<>("msg", "data");
        ApiResponse<String> response2 = new ApiResponse<>("msg", "data");

        // ASSERT
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        // ARRANGE
        ApiResponse<String> response = new ApiResponse<>("Success", "test data");

        // ACT
        String result = response.toString();

        // ASSERT
        assertTrue(result.contains("Success"));
        assertTrue(result.contains("test data"));
    }
}
