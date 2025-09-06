package com.rk.microservices.payments.data;


import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository <PaymentEntity, String>{

}
