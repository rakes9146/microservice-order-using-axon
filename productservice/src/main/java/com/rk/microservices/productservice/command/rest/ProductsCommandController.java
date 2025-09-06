package com.rk.microservices.productservice.command.rest;

import com.rk.microservices.productservice.command.CreateProductCommand;
import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products") //http://localhost:8080/products
public class ProductsCommandController {

    private final Environment environment;
    private final CommandGateway commandGateway;

    @Autowired
    public ProductsCommandController(Environment environment, CommandGateway commandGateway){
        this.environment = environment;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateProductRestModel createProductRestModel) {

        CreateProductCommand createProductCommand = CreateProductCommand.builder().price(createProductRestModel.getPrice())
                .quantity(createProductRestModel.getQuantity())
                .title(createProductRestModel.getTitle())
                .productId(UUID.randomUUID().toString()).build();

        String returnedValue = null;
        returnedValue = commandGateway.sendAndWait(createProductCommand);

        /**
        try{
         returnedValue = commandGateway.sendAndWait(createProductCommand);
        }catch (Exception e){
            e.getLocalizedMessage();
        }**/

        return returnedValue;
    }
//
//    @GetMapping
//    public String getProduct() {
//        return "HTTP Get Handled"+ environment.getProperty("local.server.port");
//    }
//
//    @PutMapping
//    public String updateProduct() {
//        return "HTTP Put Handled";
//    }
//
//    @DeleteMapping
//    public String deleteProduct() {
//        return "HTTP Put Handled";
//    }
}
