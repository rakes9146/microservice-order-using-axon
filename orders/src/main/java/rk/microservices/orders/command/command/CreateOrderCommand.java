package rk.microservices.orders.command.command;


import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@AllArgsConstructor
@Getter
public class CreateOrderCommand {


    @TargetAggregateIdentifier
    private final String orderId;
    private final String userId;
    private final String productId;
    private final int quantity;
    private final String addressId;
    private final OrderStatus orderStatus;

}
