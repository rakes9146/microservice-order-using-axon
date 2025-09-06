package com.rk.microservices.productservice.command.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class CreateProductRestModel {

    @NotBlank(message = "Product title is required field")
    private String title;

    @Min(value =1, message = "Price can not be lower than 1")
    private BigDecimal price;

    @Min(value =1, message = "Quantity can not be lower than 1")
    @Max(value =5, message = "Quantity can not be greater than 5")
    private Integer quantity;

}
