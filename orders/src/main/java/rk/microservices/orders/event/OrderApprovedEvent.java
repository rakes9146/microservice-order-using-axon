package rk.microservices.orders.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import rk.microservices.orders.command.command.OrderStatus;

@Data
@AllArgsConstructor
public class OrderApprovedEvent {

    private final String orderId;
    private final OrderStatus orderStatus =  OrderStatus.APPROVED;
}
