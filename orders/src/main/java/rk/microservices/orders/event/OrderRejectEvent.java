package rk.microservices.orders.event;

import lombok.Value;
import rk.microservices.orders.command.command.OrderStatus;

@Value
public class OrderRejectEvent {

    private final String orderId;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;


}
