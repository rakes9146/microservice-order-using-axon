package rk.microservices.orders.command.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;


@Data
@Builder
@AllArgsConstructor
public class ApprovedOrderCommand {

    @TargetAggregateIdentifier
    private final String orderId;
}
