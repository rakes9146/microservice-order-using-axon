package com.rk.microservices.productservice.query;

import com.rk.microservices.productservice.core.data.ProductEntity;
import com.rk.microservices.productservice.core.data.ProductRepository;
import com.rk.microservices.productservice.query.rest.ProductRestModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductQueryHandler {

    private final ProductRepository productRepository;

    public ProductQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductQuery findProductQuery){

        List<ProductRestModel> productRest = new ArrayList<>();
        List<ProductEntity> storedProducts = productRepository.findAll();

        productRest = storedProducts.stream()
                .map(storedProduct -> {
                    ProductRestModel productRestModel = new ProductRestModel();
                    BeanUtils.copyProperties(storedProduct, productRestModel);
                    return  productRestModel;
                })
                .collect(Collectors.toList());

        return productRest;
    }


}
