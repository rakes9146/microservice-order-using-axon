package rk.microservices.orders.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import rk.microservices.orders.core.data.OrderEntity;
import rk.microservices.orders.core.data.OrderRepository;
import rk.microservices.orders.core.data.OrderSummary;

@Component
public class OrderQueriesHandler {

    private final OrderRepository orderRepository;

    public OrderQueriesHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery){
        OrderEntity orderEntity = orderRepository.findByOrderId(findOrderQuery.getOrderId());
        return new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(), "");
    }

}
