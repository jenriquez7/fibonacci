package com.example.fibonacci.controller;

import com.example.fibonacci.service.FibonacciService;
import com.example.fibonacci.util.ResponseDto;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/v1/fibonacci", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class FibonacciController {

    private final FibonacciService fibonacciService;

    @Autowired
    public FibonacciController(FibonacciService fibonacciService) {
        this.fibonacciService = fibonacciService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto<Long>> getFibonacciValue(@PathVariable("id") @Positive Integer position) {
        try {
            Long result = fibonacciService.fibonacciValue(position, 0, 0, 1);
            return ResponseEntity.ok(new ResponseDto<>(result, HttpStatus.OK, null));
        } catch (StackOverflowError | RuntimeException e) {
            ResponseDto<Long> errorResponse = new ResponseDto<>(null, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping(value = "/consult/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto<Integer>> getConsultedTimes(@PathVariable("id") @Positive Integer position) {
        try {
            Integer result = fibonacciService.getConsultedTimes(position);
            return ResponseEntity.ok(new ResponseDto<>(result, HttpStatus.OK, null));
        } catch (RuntimeException e) {
            ResponseDto<Integer> errorResponse = new ResponseDto<>(null, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
