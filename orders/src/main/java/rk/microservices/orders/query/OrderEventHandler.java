package rk.microservices.orders.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import rk.microservices.orders.core.data.OrderEntity;
import rk.microservices.orders.core.data.OrderRepository;
import rk.microservices.orders.event.OrderApprovedEvent;
import rk.microservices.orders.event.OrderCreateEvent;
import rk.microservices.orders.event.OrderRejectEvent;

@Component
@ProcessingGroup("order-group")
public class OrderEventHandler {

    private final OrderRepository orderRepository;

    public OrderEventHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreateEvent orderCreateEvent)throws Exception{

        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(orderCreateEvent, orderEntity);
        orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderApprovedEvent orderApprovedEvent){

        OrderEntity orderEntity = orderRepository.findByOrderId(orderApprovedEvent.getOrderId());

        if(orderEntity == null){
            //to do something about it
        }

        orderEntity.setOrderStatus(orderApprovedEvent.getOrderStatus());
        orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderRejectEvent orderRejectEvent){

        OrderEntity orderEntity = orderRepository.findByOrderId(orderRejectEvent.getOrderId());

        orderEntity.setOrderStatus(orderRejectEvent.getOrderStatus());
        orderRepository.save(orderEntity);
    }
}
