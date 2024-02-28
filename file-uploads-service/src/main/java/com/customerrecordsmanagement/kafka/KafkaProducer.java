package com.customerrecordsmanagement.kafka;

import com.customerrecordsmanagement.kafka.config.FileUploadEvent;
import com.customerrecordsmanagement.kafka.config.KafkaConstants;
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

    public void publishKafkaMessage(long accountId, long fileId, long fileUploadStatusId) {
        FileUploadEvent fileUploadEvent = new FileUploadEvent(accountId, fileId, fileUploadStatusId);
        CompletableFuture<SendResult<String, FileUploadEvent>> future = kafkaTemplate.send(KafkaConstants.TOPIC, fileUploadEvent);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message=[" + fileUploadEvent + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                logger.severe("Unable to send message=[" + fileUploadEvent + "] due to : " + ex.getMessage());
            }
        });
    }
}
