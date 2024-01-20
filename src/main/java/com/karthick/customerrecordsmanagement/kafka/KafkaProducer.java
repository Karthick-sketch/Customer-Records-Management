package com.karthick.customerrecordsmanagement.kafka;

import com.karthick.customerrecordsmanagement.kafka.config.Constants;
import com.karthick.customerrecordsmanagement.kafka.config.FileUploadEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class KafkaProducer {
    private KafkaTemplate<String, FileUploadEvent> kafkaTemplate;

    private final Logger logger = Logger.getLogger(KafkaProducer.class.getName());

    public void publishKafkaMessage(long fileId, String fileName) {
        FileUploadEvent fileUploadEvent = new FileUploadEvent(fileId, fileName);
        CompletableFuture<SendResult<String, FileUploadEvent>> future = kafkaTemplate.send(Constants.TOPIC, fileUploadEvent);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message=[" + fileUploadEvent + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                logger.severe("Unable to send message=[" + fileUploadEvent + "] due to : " + ex.getMessage());
            }
        });
    }
}
