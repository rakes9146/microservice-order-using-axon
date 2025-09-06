package rk.microservices.orders.command.rest;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.hibernate.query.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rk.microservices.orders.command.command.CreateOrderCommand;
import rk.microservices.orders.command.command.OrderStatus;
import rk.microservices.orders.core.data.OrderSummary;
import rk.microservices.orders.query.FindOrderQuery;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderCommandController {

    private final Environment environment;
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public OrderCommandController(Environment environment, CommandGateway commandGateway, QueryGateway queryGateway) {
        this.environment = environment;
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public OrderSummary createOrder(@RequestBody CreateOrderRestModel createOrderRestModel) {

        String orderId = UUID.randomUUID().toString();
        CreateOrderCommand orderCommand = CreateOrderCommand.builder()
                .orderId(orderId)
                .productId(createOrderRestModel.getProductId())
                .addressId(createOrderRestModel.getAddressId())
                .quantity(createOrderRestModel.getQuantity())
                .orderStatus(OrderStatus.CREATED).build();

        String returnValue = null;
        SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult = null;
        try {
            returnValue = commandGateway.sendAndWait(orderCommand);

            queryResult = queryGateway.subscriptionQuery(new FindOrderQuery(orderId),
                    ResponseTypes.instanceOf(OrderSummary.class),
                    ResponseTypes.instanceOf(OrderSummary.class)
            );

            return queryResult.updates().blockFirst();
        } finally {
            queryResult.close();
        }
    }

}
