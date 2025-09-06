package com.rk.microservices.productservice.query.rest;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/management")
public class EventReplayController {

    @Autowired
    private EventProcessingConfiguration eventProcessingConfiguration;

    @PostMapping("/eventProcessor/{processorName}/reset")
    public ResponseEntity<String> replayEvent(@PathVariable String processorName){

        Optional<TrackingEventProcessor> trackingEventProcessorOptional  = eventProcessingConfiguration.eventProcessor(processorName, TrackingEventProcessor.class);

        if(trackingEventProcessorOptional.isPresent()){
            TrackingEventProcessor trackingEventProcessor = trackingEventProcessorOptional.get();
             trackingEventProcessor.shutDown();
             trackingEventProcessor.resetTokens();
             trackingEventProcessor.start();

             return ResponseEntity.ok().body(String.format("The event processor with a name %s has been reset", processorName));
       }else{
            return  ResponseEntity.badRequest().body(
                    String.format("The event process with name %s is not found only tracking event is supported", processorName)

            );
        }
    }

}
