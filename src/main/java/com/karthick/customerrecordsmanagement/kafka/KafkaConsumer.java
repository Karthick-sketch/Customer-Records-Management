package com.karthick.customerrecordsmanagement.kafka;

import com.karthick.customerrecordsmanagement.fileupload.FileUploadProcess;
import com.karthick.customerrecordsmanagement.kafka.config.FileUploadEvent;
import com.karthick.customerrecordsmanagement.kafka.config.KafkaConstants;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final KafkaThreadPool kafkaThreadPool;

    public KafkaConsumer(FileUploadProcess fileUploadProcess) {
        this.kafkaThreadPool = new KafkaThreadPool(fileUploadProcess);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void receiveKafkaMessage(FileUploadEvent fileUploadEvent) {
        kafkaThreadPool.execute(fileUploadEvent);
    }
}
