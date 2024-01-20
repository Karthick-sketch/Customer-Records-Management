package com.karthick.customerrecordsmanagement.kafka;

import com.karthick.customerrecordsmanagement.kafka.config.Constants;
import com.karthick.customerrecordsmanagement.kafka.config.FileUploadEvent;
import com.karthick.customerrecordsmanagement.fileupload.FileProcessService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaConsumer {
    private FileProcessService fileProcessService;

    @KafkaListener(topics = Constants.TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void receiveKafkaMessage(FileUploadEvent fileUploadEvent) {
        long fileId = fileUploadEvent.getFileId();
        String fileName = fileUploadEvent.getFileName();
        fileProcessService.pushCustomerRecordsFromFileToDatabase(fileId, fileName);
    }
}
