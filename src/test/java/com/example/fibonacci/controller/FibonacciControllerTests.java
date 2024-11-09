package com.example.fibonacci.controller;

import com.example.fibonacci.service.FibonacciService;
import com.example.fibonacci.util.ResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class FibonacciControllerTests {

    @Mock
    private FibonacciService service;

    @InjectMocks
    private FibonacciController controller;

    private int position;

    @BeforeEach
    void setUp() {
        position = 4;
    }

    @Test
    @DisplayName("Get Fibonacci value - Success number 4")
    void getFibonacciValueSuccessOfNumber4() {
        long expectedValue = 8;
        when(service.fibonacciValue(position, 0, 0L, 1L)).thenReturn(expectedValue);
        ResponseEntity<ResponseDto<Long>> response = controller.getFibonacciValue(position);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedValue, response.getBody().data());
        assertNull(response.getBody().error());
    }

    @Test
    @DisplayName("Get Fibonacci value - Runtime Exception")
    void getFibonacciValueRuntimeException() {
        String errorMessage = "Error Runtime Exception";
        when(service.fibonacciValue(position, 0, 0L, 1L)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<ResponseDto<Long>> response = controller.getFibonacciValue(position);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().data());
        assertEquals(errorMessage, response.getBody().error());
    }

    @Test
    @DisplayName("Get Fibonacci value - Stack Overflow")
    void getFibonacciValueStackOverflowError() {
        String errorMessage = "Error Stack Overflow";
        when(service.fibonacciValue(position, 0, 0L, 1L)).thenThrow(new StackOverflowError(errorMessage));

        ResponseEntity<ResponseDto<Long>> response = controller.getFibonacciValue(position);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().data());
        assertEquals(errorMessage, response.getBody().error());
    }

    @Test
    @DisplayName("Get Consulted Times - Success")
    void getConsultedTimesSuccess() {
        Integer expectedTimes = 3;
        when(service.getConsultedTimes(position)).thenReturn(expectedTimes);

        ResponseEntity<ResponseDto<Integer>> response = controller.getConsultedTimes(position);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedTimes, response.getBody().data());
        assertNull(response.getBody().error());
    }

    @Test
    @DisplayName("Get Consulted Times - Runtime Exception")
    void getConsultedTimesRuntimeException() {
        String errorMessage = "Error consultando contador";
        when(service.getConsultedTimes(position)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<ResponseDto<Integer>> response = controller.getConsultedTimes(position);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().data());
        assertEquals(errorMessage, response.getBody().error());
    }
}
