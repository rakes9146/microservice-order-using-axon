package com.rk.microservices.productservice.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "productlookup")
public class ProductLookupEntity  implements Serializable {

    private static final long serialVersionUID = 3324253243242L;

    @Id
    private String productId;

    @Column(unique = true)
    private String title;

}
