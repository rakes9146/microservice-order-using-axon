package rk.microservices.orders.command.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import rk.microservices.orders.event.OrderApprovedEvent;
import rk.microservices.orders.event.OrderCreateEvent;
import rk.microservices.orders.event.OrderRejectEvent;

@Component
@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {

        OrderCreateEvent orderCreateEvent =new OrderCreateEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreateEvent);
        AggregateLifecycle.apply(orderCreateEvent);
    }


    @EventSourcingHandler
    public void on(OrderCreateEvent orderCreateEvent){

        this.orderId =orderCreateEvent.getOrderId();
        this.productId = orderCreateEvent.getProductId();
        this.userId = orderCreateEvent.getUserId();
        this.quantity = orderCreateEvent.getQuantity();
        this.addressId = orderCreateEvent.getAddressId();
        this.orderStatus = orderCreateEvent.getOrderStatus();
        this.orderStatus = orderCreateEvent.getOrderStatus();
    }

    @CommandHandler
    public void handle(ApprovedOrderCommand approvedOrderCommand){
        //create and publish the orderapprovedevent
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(approvedOrderCommand.getOrderId());
        AggregateLifecycle.apply(orderApprovedEvent);
    }

    @CommandHandler
    public void handle(RejectOrderCommand rejectOrderCommand){

        OrderRejectEvent orderRejectEvent = new OrderRejectEvent(rejectOrderCommand.getOrderId(),
                rejectOrderCommand.getReason());
        AggregateLifecycle.apply(orderRejectEvent);
    }

    @EventSourcingHandler
    protected void on(OrderRejectEvent orderRejectEvent){

        this.orderStatus = orderRejectEvent.getOrderStatus();
    }

    @EventSourcingHandler
    protected void on(OrderApprovedEvent orderApprovedEvent){

        this.orderStatus = orderApprovedEvent.getOrderStatus();
    }
}
