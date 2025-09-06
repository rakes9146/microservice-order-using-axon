package com.rk.microservices.productservice.query.rest;


import com.rk.microservices.productservice.query.FindProductQuery;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductQueryController {

  private final QueryGateway queryGateway;

    public ProductQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public List<ProductRestModel>  getProducts(){

      FindProductQuery findProuctQuery= new FindProductQuery();
      List<ProductRestModel> products = queryGateway.query(findProuctQuery,
              ResponseTypes.multipleInstancesOf(ProductRestModel.class)).join();

      return products;
  }


}
