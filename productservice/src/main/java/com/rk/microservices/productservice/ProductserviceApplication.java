package com.rk.microservices.productservice;

import com.rk.microservices.productservice.command.interceptor.CreateProductCommandInterceptor;
import com.rk.microservices.productservice.core.errorhandler.ProductServiceEventsErrorHandler;
import com.thoughtworks.xstream.XStream;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@EnableDiscoveryClient
@SpringBootApplication
public class ProductserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductserviceApplication.class, args);
	}

	@Autowired
	public void registerCreateProductCommandInterceptor(ApplicationContext contexta,
														CommandBus commandBus){
		commandBus.registerDispatchInterceptor(contexta.getBean(CreateProductCommandInterceptor.class));
	}
//
	@Autowired
	public void configure(EventProcessingConfigurer configure) {

		configure.registerListenerInvocationErrorHandler("product-group",
				conf -> new ProductServiceEventsErrorHandler());

////		configure.registerListenerInvocationErrorHandler("product-group",
////				conf -> PropagatingErrorHandler.instance());
//
//	}
	}


	@Bean(name = "productSnapshotTriggerDefinition")
	public SnapshotTriggerDefinition productSnapshotTriggerDefinition(Snapshotter snapshotter){

		return new EventCountSnapshotTriggerDefinition(snapshotter, 3);
	}


}
