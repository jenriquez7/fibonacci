package com.example.fibonacci.service;

import com.example.fibonacci.model.Fibonacci;
import com.example.fibonacci.repository.FibonacciRepository;
import com.example.fibonacci.service.impl.FibonacciServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataAccessException;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class FibonacciServiceTests {

    @Mock
    private FibonacciRepository repository;

    @InjectMocks
    private FibonacciServiceImpl service;

    private Fibonacci fibonacci;
    private int position;

    @BeforeEach
    void setUp() {
        position = 4;
        fibonacci = new Fibonacci(position, 8L, 3);
    }

    @Test
    @DisplayName("Get Fibonacci Value From Cache - Success")
    void getFibonacciValueFromCacheSuccess() {
        when(repository.findById(position)).thenReturn(Optional.of(fibonacci));
        when(repository.save(any(Fibonacci.class))).thenReturn(fibonacci);

        Long result = service.fibonacciValue(position, 0, 0, 1);

        assertEquals(fibonacci.getValue(), result);
        verify(repository).findById(position);
        verify(repository).save(argThat(f -> f.getTimes() == 4));
    }

    @Test
    @DisplayName("Get Fibonacci Value Generated - Success")
    void getFibonacciValueGeneratedSuccess() {
        when(repository.findById(position)).thenReturn(Optional.empty());
        when(repository.save(any(Fibonacci.class))).thenReturn(fibonacci);

        Long result = service.fibonacciValue(position, 0, 0, 1);

        assertEquals(fibonacci.getValue(), result);
        verify(repository, times(position + 1)).findById(position);
    }

    @Test
    @DisplayName("Get Fibonacci Value Saving Intermediates Values - Success")
    void getFibonacciValueSavesIntermediateValuesSuccess() {
        when(repository.findById(position)).thenReturn(Optional.empty());
        when(repository.existsById(position)).thenReturn(false);
        when(repository.save(any(Fibonacci.class))).thenReturn(fibonacci);

        Long result = service.fibonacciValue(position, 0, 0, 1);

        assertEquals(fibonacci.getValue(), result);
        verify(repository, times(position + 1)).findById(any());
        verify(repository, atLeastOnce()).save(any(Fibonacci.class));
    }

    @Test
    @DisplayName("Get Fibonacci Value - Stack Overflow")
    void getFibonacciValueStackOverflow() {
        when(repository.findById(position)).thenReturn(Optional.empty());
        when(repository.existsById(position)).thenReturn(false);
        when(repository.save(any())).thenThrow(new StackOverflowError("Stack overflow"));

        assertThrows(StackOverflowError.class, () -> service.fibonacciValue(position, 0, 0, 1));
    }

    @Test
    @DisplayName("Get Fibonacci Value - Unexpected Error")
    void getFibonacciValueUnexpectedError() {
        when(repository.findById(any())).thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.fibonacciValue(position, 0, 0, 1)
        );
        assertEquals("Error inesperado", exception.getMessage());
    }

    @Test
    @DisplayName("Get Consulted Times - Success")
    void getConsultedTimesSuccess() {
        when(repository.findById(position)).thenReturn(Optional.of(fibonacci));

        Integer result = service.getConsultedTimes(position);

        assertEquals(fibonacci.getTimes(), result);
        verify(repository).findById(position);
    }

    @Test
    @DisplayName("Get Consulted Times - Not Exists")
    void getConsultedTimesNotExist() {
        when(repository.findById(position)).thenReturn(Optional.empty());

        Integer result = service.getConsultedTimes(position);

        assertEquals(0, result);
        verify(repository).findById(position);
    }

    @Test
    @DisplayName("Get Consulted Times - Data Access Error")
    void getConsultedTimesDataAccessError() {
        when(repository.findById(position)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(DataAccessException.class, () -> service.getConsultedTimes(position));
    }

    @Test
    @DisplayName("Get Consulted Times - Unexpected Error")
    void getConsultedTimesUnexpectedError() {
        when(repository.findById(position)).thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.getConsultedTimes(position)
        );
        assertEquals("Error inesperado", exception.getMessage());
    }
}
