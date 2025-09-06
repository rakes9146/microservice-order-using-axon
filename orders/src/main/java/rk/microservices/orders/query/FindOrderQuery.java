package rk.microservices.orders.query;

import lombok.Value;

@Value
public class FindOrderQuery {

    private final String orderId;

}
