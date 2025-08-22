package com.myapp.service;

import com.myapp.exp.ValidationException;

public class ValidationService {
    public void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Поле '" + field + "' не може бути порожнім.");
        }
    }
}