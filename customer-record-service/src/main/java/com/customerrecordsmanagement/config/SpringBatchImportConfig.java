package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customerrecords.CustomerRecordRepository;
import com.customerrecordsmanagement.customerrecords.CustomerRecordService;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Nonnull
    private CustomerRecordService customerRecordService;
    @Nonnull
    private CustomFieldMappingService customFieldMappingService;

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
                .processor(itemProcessor(accountId))
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

    @Bean(name = "importItemReader")
    @StepScope
    public FlatFileItemReader<Map<String, String>> itemReader(@Value("#{jobParameters[accountId]}") Long accountId, @Value("#{jobParameters[filePath]}") String filePath) {
        FlatFileItemReader<Map<String, String>> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(filePath));
        reader.setLinesToSkip(1);

        List<String> customerRecordFieldNames = CustomerRecord.getFields();
        List<String> customFieldNames = customFieldMappingService.fetchCustomFieldNamesByAccountId(accountId);

        String[] headers = getHeaders(filePath);
        verifyHeaders(headers, Stream.concat(customerRecordFieldNames.stream(), customFieldNames.stream()).toList());

        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(headers);
            }});
            setFieldSetMapper(fieldSet -> Arrays.stream(headers)
                        .collect(Collectors.toMap(header -> header, fieldSet::readString)));
        }});
        return reader;
    }

    @Bean(name = "importItemProcessor")
    @StepScope
    public ItemProcessor<Map<String, String>, CustomerRecord> itemProcessor(@Value("#{jobParameters[accountId]}") Long accountId) {
        return stringMap -> {
            stringMap.put("accountId", accountId.toString());
            return customerRecordService.convertMapToCustomerRecord(stringMap);
        };
    }

    @Bean(name = "importItemWriter")
    @StepScope
    public ItemWriter<CustomerRecord> itemWriter() {
        RepositoryItemWriter<CustomerRecord> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRecordRepository);
        writer.setMethodName("save");
        return writer;
    }

    private void verifyHeaders(String[] headers, List<String> customerRecordFields) {
        for (String header : headers) {
            if (!(header.equals("accountId") || customerRecordFields.contains(header))) {
                throw new RuntimeException("Unknown header '" + header + "'");
            }
        }
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
