package com.customerrecordsmanagement.fileuploadstatus.fileprocess;

import com.customerrecordsmanagement.config.FileUploadEvent;
import lombok.Setter;

public class FileUploadEventThreadPool {
    private final FileUploadProcess fileUploadProcess;
    private final ThreadPool[] threadPools = new ThreadPool[5];

    public FileUploadEventThreadPool(FileUploadProcess fileUploadProcess) {
        this.fileUploadProcess = fileUploadProcess;
        for (int i = 0; i < threadPools.length; i++) {
            threadPools[i] = new ThreadPool();
        }
    }

    public void execute(FileUploadEvent fileUploadEvent) {
        for (ThreadPool threadPool : threadPools) {
            if (!threadPool.isAlive()) {
                threadPool.setFileUploadEvent(fileUploadEvent);
                threadPool.start();
                return;
            }
        }
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        execute(fileUploadEvent);
    }

    @Setter
    class ThreadPool extends Thread {
        private FileUploadEvent fileUploadEvent;

        @Override
        public void run() {
            long accountId = fileUploadEvent.getAccountId();
            long csvFileDetailId = fileUploadEvent.getCsvFileDetailId();
            long fileUploadStatusId = fileUploadEvent.getFileUploadStatusId();
            fileUploadProcess.pushCustomerRecordsFromFileToDatabase(accountId, csvFileDetailId, fileUploadStatusId);
        }
    }
}