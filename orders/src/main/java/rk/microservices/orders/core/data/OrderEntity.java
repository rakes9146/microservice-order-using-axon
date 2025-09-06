package rk.microservices.orders.core.data;


import jakarta.persistence.*;
import lombok.Data;
import org.checkerframework.common.aliasing.qual.Unique;
import rk.microservices.orders.command.command.OrderStatus;

@Entity
@Table(name = "orders")
@Data
public class OrderEntity {

    private static final long serialVersionUID = -834245123456789L;

    @Id
    @Column(unique = true)
    public String orderId;

    private String userId;
    private String quantity;
    private String addressId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;


}
