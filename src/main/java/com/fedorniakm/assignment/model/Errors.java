package com.fedorniakm.assignment.model;

import java.util.ArrayList;
import java.util.List;

public class Errors {
    private record Detail(int status, String message, String detail) { }

    private final List<Detail> errors;

    public Errors() {
        this.errors = new ArrayList<>();
    }

    public void addError(int status, String message, String detail) {
        errors.add(new Detail(status, message, detail));
    }

    public List<Detail> getErrors() {
        return errors;
    }
}
