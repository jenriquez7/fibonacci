package com.example.fibonacci.util;

import org.springframework.http.HttpStatus;

public record ResponseDto<T>(T data, HttpStatus status, String error) { }
