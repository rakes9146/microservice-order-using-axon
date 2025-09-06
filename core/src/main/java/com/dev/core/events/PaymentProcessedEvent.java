package com.dev.core.events;


import com.dev.core.user.PaymentDetails;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;


@Data
public class PaymentProcessedEvent {

    @TargetAggregateIdentifier
    private final String paymentId;
    private final String orderId;
}
