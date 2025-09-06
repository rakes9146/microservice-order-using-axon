package com.rk.microservices.productservice.command.interceptor;

import com.rk.microservices.productservice.command.CreateProductCommand;
import com.rk.microservices.productservice.core.data.ProductLookupEntity;
import com.rk.microservices.productservice.core.data.ProductLookupRepository;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.net.BindException;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger logger =  LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    private final ProductLookupRepository productLookupRepository;

    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {

        return (index, command) ->{
            logger.info("Intercepted Command: "+command.getPayloadType());
            if(CreateProductCommand.class.equals(command.getPayloadType())){

                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                ProductLookupEntity productLookupEntity =
                        productLookupRepository.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());
                if(productLookupEntity != null){
                    throw new IllegalStateException(
                            String.format("Product with product id %s or title %s already exists ",
                            createProductCommand.getProductId(),  createProductCommand.getTitle())
                            );
                }
            }

            return command;
        };
    }
}
