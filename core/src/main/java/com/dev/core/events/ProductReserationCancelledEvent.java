package com.dev.core.events;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class ProductReserationCancelledEvent {

    @TargetAggregateIdentifier
    private final String productId;
    private final int quantity;
    private final String orderId;
    private final String userId;
    private final String reason;


}
