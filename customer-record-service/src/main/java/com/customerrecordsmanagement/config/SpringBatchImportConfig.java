package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customerrecords.CustomerRecordRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class SpringBatchImportConfig {
    @Nonnull
    private JobRepository jobRepository;
    @Nonnull
    private PlatformTransactionManager transactionManager;
    @Nonnull
    private CustomerRecordRepository customerRecordRepository;
//    @Nonnull
//    private CustomFieldMappingService customFieldMappingService;

    private Long accountId;
    private String filePath;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobParameters parameters = stepExecution.getJobExecution().getJobParameters();
        this.accountId = parameters.getLong("accountId");
        this.filePath = parameters.getString("filePath");
    }

    @Bean(name = "customerRecordImportJob")
    public Job runJob() {
        return new JobBuilder("customerRecordImportJob", jobRepository)
                .start(step()).build();
    }

    @Async
    @Bean(name = "customerRecordImportStep")
    public Step step() {
        return new StepBuilder("customerRecordImportStep", jobRepository)
                .<Map<String, String>, CustomerRecord>chunk(100, transactionManager)
                .reader(itemReader(accountId, filePath))
                .processor(itemProcessor())
                .writer(itemWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean(name = "importTaskExecutor")
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Map<String, String>> itemReader(@Value("#{jobParameters[accountId]}") Long accountId, @Value("#{jobParameters[filePath]}") String filePath) {
        return new FlatFileItemReaderBuilder<Map<String, String>>()
                .name("customerRecordItemReader")
                .resource(new FileSystemResource(filePath))
                .linesToSkip(1)
                .lineMapper(lineMapper(accountId, filePath))
                .build();
    }

    @Bean
    @StepScope
    public CustomerRecordItemProcessor itemProcessor() {
        return new CustomerRecordItemProcessor();
    }

    @Bean
    @StepScope
    public ItemWriter<CustomerRecord> itemWriter() {
        RepositoryItemWriter<CustomerRecord> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRecordRepository);
        writer.setMethodName("save");
        return writer;
    }

    private LineMapper<Map<String, String>> lineMapper(Long accountId, String filePath) {
//        List<String> customerRecordFieldNames = CustomerRecord.getFields();
//        List<String> customFieldNames = customFieldMappingService.fetchCustomFieldNamesByAccountId(accountId);
//        List<String> headers = Stream.concat(customerRecordFieldNames.stream(), customFieldNames.stream()).toList();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(getHeaders(filePath));
//        lineTokenizer.setNames(headers.toArray(String[]::new));

        BeanWrapperFieldSetMapper<Map<String, String>> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType((Class<? extends Map<String, String>>) Map.class);

        DefaultLineMapper<Map<String, String>> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }

    private String[] getHeaders(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.hasNextLine()) {
                String headers = scanner.nextLine();
                return headers.split(",");
            } else {
                throw new RuntimeException("File is empty");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found [" + filePath + "]");
        }
    }
}
