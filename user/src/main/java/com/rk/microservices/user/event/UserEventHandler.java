package com.rk.microservices.user.event;

import com.dev.core.query.FetchUserPaymentsDetailsQuery;
import com.dev.core.user.PaymentDetails;
import com.dev.core.user.User;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserEventHandler {

    @QueryHandler
    public User getUserDetails(FetchUserPaymentsDetailsQuery fetchUserPaymentsDetailsQuery)
    {
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("SERGEY KARGOPOLOV")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        User userRest = User.builder()
                .firstName("Sergey")
                .lastName("Kargopolov")
                .userId(fetchUserPaymentsDetailsQuery.getUserId())
                .paymentsDetails(paymentDetails)
                .build();


        return userRest;

    }

}
