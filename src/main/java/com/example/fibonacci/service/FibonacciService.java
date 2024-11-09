package com.example.fibonacci.service;

public interface FibonacciService {
    Long fibonacciValue(Integer position, int count, long prev, long next);
    Integer getConsultedTimes(Integer position);
}
