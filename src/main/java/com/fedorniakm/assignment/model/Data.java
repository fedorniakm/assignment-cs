package com.fedorniakm.assignment.model;

import jakarta.validation.Valid;

public record Data<T>(@Valid T data) {
    public static <T> Data<T> of(T data) {
        return new Data<>(data);
    }
}
