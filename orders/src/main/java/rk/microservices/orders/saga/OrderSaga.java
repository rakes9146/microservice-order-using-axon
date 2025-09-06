package rk.microservices.orders.saga;

import com.dev.core.CancelProductReservationCommand;
import com.dev.core.ProcessPaymentCommand;
import com.dev.core.ReserveProductCommand;
import com.dev.core.events.PaymentProcessedEvent;
import com.dev.core.events.ProductReserationCancelledEvent;
import com.dev.core.events.ProductReserveEvent;
import com.dev.core.query.FetchUserPaymentsDetailsQuery;
import com.dev.core.user.User;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import rk.microservices.orders.command.command.ApprovedOrderCommand;
import rk.microservices.orders.command.command.RejectOrderCommand;
import rk.microservices.orders.core.data.OrderSummary;
import rk.microservices.orders.event.OrderApprovedEvent;
import rk.microservices.orders.event.OrderCreateEvent;
import rk.microservices.orders.event.OrderRejectEvent;
import rk.microservices.orders.query.FindOrderQuery;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.logging.Logger;

@Saga
@Component
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private static final Logger LOGGER = Logger.getLogger(OrderSaga.class.getName());

    private final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE="payment-processing-deadline";

    String scheduledId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreateEvent orderCreateEvent){

        ReserveProductCommand reserveProductCommand = ReserveProductCommand.
                        builder()
                .orderId(orderCreateEvent.getOrderId())
                .productId(orderCreateEvent.getProductId())
                .quantity(orderCreateEvent.getQuantity())
                .userId(orderCreateEvent.getUserId())
                .build();

        LOGGER.info("OrderCreatedEvent handled for order Id "+reserveProductCommand.getOrderId() + " "+" Any Product Id: "+reserveProductCommand.getProductId());

        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if(commandResultMessage.isExceptional()){
                    RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(reserveProductCommand.getOrderId(),
                            commandResultMessage.exceptionResult().getMessage());

                    commandGateway.send(rejectOrderCommand);

                }
            }
        });
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReserveEvent productReserveEvent){
            //process user payments
        LOGGER.info("ProductReserveEvent called for product "+productReserveEvent.getProductId()+
                "and order id "+productReserveEvent.getOrderId());

        FetchUserPaymentsDetailsQuery fetchUserPaymentsDetailsQuery =
                new FetchUserPaymentsDetailsQuery(productReserveEvent.getUserId());

        User userPaymentDetails = null;
        try{
            userPaymentDetails = queryGateway.query(fetchUserPaymentsDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        }catch (Exception e){
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
            cancelProductReservation(productReserveEvent, e.getMessage());
            return;
        }

        if(userPaymentDetails == null){
            cancelProductReservation(productReserveEvent, "Could not fetch user details");
            return;
        }
        LOGGER.info("Successfully fetched user payments details for user "+userPaymentDetails.getLastName());

     scheduledId =  deadlineManager.schedule(Duration.of(120, ChronoUnit.SECONDS) ,
                PAYMENT_PROCESSING_TIMEOUT_DEADLINE, productReserveEvent);;

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReserveEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentsDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();
      //  String result = null;
        try {
           // result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
            commandGateway.send(processPaymentCommand, new CommandCallback<ProcessPaymentCommand, Object>() {
                @Override
                public void onResult(@Nonnull CommandMessage<? extends ProcessPaymentCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                    LOGGER.info("The processing payment has been failed starting compensating transaction");

                    if(commandResultMessage.isExceptional()){
                        cancelProductReservation(productReserveEvent, commandResultMessage.exceptionResult().getMessage());
                    }
                }
            });
        }catch (Exception e){
                cancelProductReservation(productReserveEvent, e.getMessage());
                return;
        }
        }



   private void cancelProductReservation(ProductReserveEvent productReserveEvent, String reason){
       cancelDeadline();
       CancelProductReservationCommand publishProductReservationCommand =
               CancelProductReservationCommand.builder()
                       .orderId(productReserveEvent.getOrderId())
                       .productId(productReserveEvent.getProductId())
                       .quantity(productReserveEvent.getQuantity())
                       .userId(productReserveEvent.getUserId())
                       .reason(reason)
                       .build();

        commandGateway.send(publishProductReservationCommand);
   }

  @SagaEventHandler(associationProperty = "orderId")
  public void handle(PaymentProcessedEvent paymentProcessedEvent){
        cancelDeadline();
        //send approve order command
        ApprovedOrderCommand approvedOrderCommand = new ApprovedOrderCommand
                (paymentProcessedEvent.getOrderId());
        commandGateway.send(approvedOrderCommand);
  }

  private  void cancelDeadline(){
     if(scheduledId != null) {
         deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduledId);
     }
    }

  @EndSaga
  @SagaEventHandler(associationProperty = "orderId")
  public void handle(OrderApprovedEvent orderApprovedEvent){

        LOGGER.info("Order is approved. Order saga is complete orders "+orderApprovedEvent.getOrderId());
       //   SagaLifecycle.end();
        queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
                new OrderSummary(orderApprovedEvent.getOrderId(),
                        orderApprovedEvent.getOrderStatus(), ""));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReserationCancelledEvent productReserationCancelledEvent){
        //create and sent reject command
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReserationCancelledEvent.getOrderId(),
                productReserationCancelledEvent.getReason());

        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectEvent orderRejectEvent){
        LOGGER.info("Successfully Rejected order with ID "+orderRejectEvent.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
                new OrderSummary(orderRejectEvent.getOrderId(), orderRejectEvent.getOrderStatus(),
                        orderRejectEvent.getReason()));

    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
    public void handlePaymentDeadline(ProductReserveEvent productReserveEvent){
        LOGGER.severe("Payment processing deadline took place, Sending a compensating command to cancle the product");
         cancelProductReservation(productReserveEvent, "Payment timeout");
    }


}
