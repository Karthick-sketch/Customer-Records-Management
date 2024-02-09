package com.karthick.customerrecordsmanagement.kafka;

import com.karthick.customerrecordsmanagement.fileupload.FileUploadProcess;
import com.karthick.customerrecordsmanagement.kafka.config.Constants;
import com.karthick.customerrecordsmanagement.kafka.config.FileUploadEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaConsumer {
    private FileUploadProcess fileUploadProcess;

    @KafkaListener(topics = Constants.TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void receiveKafkaMessage(FileUploadEvent fileUploadEvent) {
        long fileId = fileUploadEvent.getFileUploadStatusId();
        long fileUploadStatusId = fileUploadEvent.getFileUploadStatusId();
        fileUploadProcess.pushCustomerRecordsFromFileToDatabase(fileId, fileUploadStatusId);
    }
}
