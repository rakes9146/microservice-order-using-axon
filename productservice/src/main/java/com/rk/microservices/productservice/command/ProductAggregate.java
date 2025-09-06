package com.rk.microservices.productservice.command;

import com.dev.core.CancelProductReservationCommand;
import com.dev.core.ReserveProductCommand;
import com.dev.core.events.ProductReserationCancelledEvent;
import com.dev.core.events.ProductReserveEvent;
import com.rk.microservices.productservice.core.events.ProductCreateEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Aggregate(snapshotTriggerDefinition = "productSnapshotTriggerDefinition")
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    public ProductAggregate() {
    }

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand){
        //create product
        if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price can not be less or equal then zero");
        }

        if (createProductCommand.getTitle() == null || createProductCommand.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title can not be empty");
        }

        ProductCreateEvent productCreateEvent = new ProductCreateEvent();
        BeanUtils.copyProperties(createProductCommand, productCreateEvent);
        AggregateLifecycle.apply(productCreateEvent);

    }

    @CommandHandler
    public void handle(ReserveProductCommand reserveProductCommand){

        if(quantity.intValue() < reserveProductCommand.getQuantity()){
            throw new IllegalArgumentException("Insufficient number of items in stock");
        }

        ProductReserveEvent productReserveEvent = ProductReserveEvent.builder()
                .orderId(reserveProductCommand.getOrderId())
                .productId(reserveProductCommand.getProductId())
                .quantity(reserveProductCommand.getQuantity())
                .userId(reserveProductCommand.getUserId())
                .build();

        AggregateLifecycle.apply(productReserveEvent);
    }

    @CommandHandler
    public void handle(CancelProductReservationCommand cancelProductReservationCommand){

        ProductReserationCancelledEvent productReserationCancelledEvent =
                    ProductReserationCancelledEvent.builder()
                            .orderId(cancelProductReservationCommand.getOrderId())
                            .productId(cancelProductReservationCommand.getProductId())
                            .quantity(cancelProductReservationCommand.getQuantity())
                            .reason(cancelProductReservationCommand.getReason())
                            .userId(cancelProductReservationCommand.getUserId())
                            .build();

        AggregateLifecycle.apply(productReserationCancelledEvent);
    }


    @EventSourcingHandler
    public void on(ProductReserationCancelledEvent productReserationCancelledEvent){
         this.quantity+= productReserationCancelledEvent.getQuantity();
    }


    @EventSourcingHandler
    public void on(ProductCreateEvent productCreateEvent){
        this.productId = productCreateEvent.getProductId();
        this.price = productCreateEvent.getPrice();
        this.title = productCreateEvent.getTitle();
        this.quantity = productCreateEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(ProductReserveEvent productReserveEvent){

        this.quantity -= productReserveEvent.getQuantity();
    }
}
