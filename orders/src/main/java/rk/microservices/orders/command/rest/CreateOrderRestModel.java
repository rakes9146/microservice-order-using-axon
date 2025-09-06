package rk.microservices.orders.command.rest;

import lombok.Data;
import rk.microservices.orders.command.command.OrderStatus;

@Data
public class CreateOrderRestModel {
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

}
