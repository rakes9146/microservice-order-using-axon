package com.rk.microservices.productservice.core.errorhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorMessage {

    private final Date timestamp;
    private String  message;
}
