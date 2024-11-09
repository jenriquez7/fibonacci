package com.example.fibonacci.service.impl;

import com.example.fibonacci.model.Fibonacci;
import com.example.fibonacci.repository.FibonacciRepository;
import com.example.fibonacci.service.FibonacciService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class FibonacciServiceImpl implements FibonacciService {

    private final FibonacciRepository fibonacciRepository;

    @Autowired
    public FibonacciServiceImpl(FibonacciRepository fibonacciRepository) {
        this.fibonacciRepository = fibonacciRepository;
    }

    @Override
    @Cacheable(value = "fibonacciCache", key = "#position")
    public Long fibonacciValue(Integer position, int count, long prev, long next) {
        try {
            Optional<Fibonacci> cachedResult = fibonacciRepository.findById(position);
            if (cachedResult.isPresent()) {
                Fibonacci result = cachedResult.get();
                result.setTimes(result.getTimes() + 1);
                fibonacciRepository.save(result);
                return cachedResult.get().getValue();
            }

            if (count == position) {
                long result = prev + next;
                fibonacciRepository.save(new Fibonacci(count, result, 1));
                return result;
            }

            if (!fibonacciRepository.existsById(count) && count > 0) {
                fibonacciRepository.save(new Fibonacci(count, prev + next, 0));
            }

            return this.fibonacciValue(position, count + 1, next, prev + next);

        } catch (StackOverflowError e) {
            log.error("Sin memoria para calcular la posición {}: {}", position, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Error inesperado {}: {}", position, e.getMessage());
            throw new RuntimeException("Error inesperado");
        }
    }

    @Override
    public Integer getConsultedTimes(Integer position) {
        try {
            Optional<Fibonacci> fibonacci = fibonacciRepository.findById(position);
            if (fibonacci.isPresent()) {
                return fibonacci.get().getTimes();
            } else {
                return 0;
            }

        } catch (DataAccessException e) {
            log.error("Error al acceder a posición {} en la base de datos: {}", position, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Error inesperado {}: {}", position, e.getMessage());
            throw new RuntimeException("Error inesperado", e);
        }
    }
}
