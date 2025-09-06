package com.rk.microservices.productservice.query;

import com.dev.core.events.ProductReserationCancelledEvent;
import com.dev.core.events.ProductReserveEvent;
import com.rk.microservices.productservice.core.data.ProductEntity;
import com.rk.microservices.productservice.core.data.ProductRepository;
import com.rk.microservices.productservice.core.events.ProductCreateEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {

    private final ProductRepository productRepository;


    private static final Logger LOGGER = Logger.getLogger(ProductCreateEvent.class.getName());

    public ProductEventHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @ExceptionHandler(resultType =  Exception.class)
    public void handle(Exception exception) {
        //logg general error message
    }
    @ExceptionHandler(resultType = IllegalStateException.class)
    public void handle(IllegalStateException illegalStateException){
        //log error message
    }



    @EventHandler
    public void on(ProductCreateEvent productCreateEvent) throws Exception {

        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productCreateEvent, productEntity);
        productRepository.save(productEntity);

        if(true)
            throw new Exception("Forcing events in products class");
    }


    @EventHandler
    public void on(ProductReserveEvent productReserveEvent){
        ProductEntity productEntity = productRepository.findByProductId(productReserveEvent.getProductId());
        LOGGER.info("ProductReserveEvent: current product quantity "+productEntity.getQuantity());

        productEntity.setQuantity(productEntity.getQuantity() - productReserveEvent.getQuantity());
        productRepository.save(productEntity);

        LOGGER.info("ProductReserveEvent: New product quantity "+productEntity.getQuantity());

        LOGGER.info("ProductReserveEvent is called for productId: "+productReserveEvent.getProductId());
    }


    @EventHandler
    public void on(ProductReserationCancelledEvent productReserationCancelledEvent){

         ProductEntity currProductEntity = productRepository.findByProductId(productReserationCancelledEvent.getProductId());
        LOGGER.info("ProductReservationCancleEvent: current product quantity "+currProductEntity.getQuantity());
        int newQuantity =  currProductEntity.getQuantity() + productReserationCancelledEvent.getQuantity();
         currProductEntity.setQuantity(newQuantity);
         productRepository.save(currProductEntity);
        LOGGER.info("ProductReservationCancleEvent: new product quantity "
                +currProductEntity.getQuantity());

    }


    @ResetHandler
    public void reset(){
        productRepository.deleteAll();
    }

}
