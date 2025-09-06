package rk.microservices.orders.core.data;

import lombok.Value;
import rk.microservices.orders.command.command.OrderStatus;

@Value
public class OrderSummary {

    private String orderId;
    private OrderStatus orderStatus;
    private String message;
}
