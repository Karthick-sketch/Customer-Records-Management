package com.customerrecordsmanagement.fileprocess;

import com.customerrecordsmanagement.config.FileUploadEvent;
import com.customerrecordsmanagement.config.KafkaConstants;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FileUploadEventKafkaConsumer {
    private final FileUploadEventThreadPool fileUploadEventThreadPool;

    public FileUploadEventKafkaConsumer(FileUploadProcess fileUploadProcess) {
        this.fileUploadEventThreadPool = new FileUploadEventThreadPool(fileUploadProcess);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void receiveKafkaMessage(FileUploadEvent fileUploadEvent) {
        fileUploadEventThreadPool.execute(fileUploadEvent);
    }
}
