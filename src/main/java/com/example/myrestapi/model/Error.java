package com.example.myrestapi.model;

import lombok.Data;
import org.springframework.lang.NonNull;


@Data
public class Error {
    @NonNull
    Integer code;
    @NonNull
    String message;
}
