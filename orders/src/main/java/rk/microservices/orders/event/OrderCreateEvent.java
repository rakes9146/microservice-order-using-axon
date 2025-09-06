package rk.microservices.orders.event;

import lombok.Data;
import rk.microservices.orders.command.command.OrderStatus;

@Data
public class OrderCreateEvent {

    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;
}
