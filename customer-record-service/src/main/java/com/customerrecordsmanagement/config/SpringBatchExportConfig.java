package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
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
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

@Configuration
@EnableBatchProcessing
public class SpringBatchExportConfig {
    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CustomerRecordMapper customerRecordMapper;
    private final CustomFieldMappingService customFieldMappingService;
    private Long accountId;
    private String filePath;

    public SpringBatchExportConfig(DataSource dataSource,
            @Qualifier("batchExportJobRepository") JobRepository jobRepository,
            @Qualifier("batchExportTransactionManager") PlatformTransactionManager transactionManager,
            CustomerRecordMapper customerRecordMapper, CustomFieldMappingService customFieldMappingService) {
        this.dataSource = dataSource;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.customerRecordMapper = customerRecordMapper;
        this.customFieldMappingService = customFieldMappingService;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobParameters parameters = stepExecution.getJobExecution().getJobParameters();
        this.accountId = parameters.getLong("accountId");
        this.filePath = parameters.getString("filePath");
    }

    @Bean(name = "customerRecordExportJob")
    public Job job() throws Exception {
        return new JobBuilder("customerRecordExportJob", jobRepository)
                .start(step()).build();
    }

    @Async
    @Bean(name = "customerRecordExportStep")
    public Step step() throws Exception {
        return new StepBuilder("customerRecordExportStep", jobRepository)
                .<CustomerRecord, CustomerRecord>chunk(100, transactionManager)
                .reader(dbReader(accountId))
                .processor(itemProcessor())
                .writer(fileWriter(accountId, filePath))
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean(name = "exportTaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(8);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("MultiThreaded-");
        return executor;
    }

    @Bean(name = "exportItemProcessor")
    @StepScope
    public ItemProcessor<CustomerRecord, CustomerRecord> itemProcessor() {
        return customerRecord -> customerRecord;
    }

    @Bean(name = "exportItemWriter")
    @StepScope
    public FlatFileItemWriter<CustomerRecord> fileWriter(@Value("#{jobParameters[accountId]}") Long accountId,
            @Value("#{jobParameters[filePath]}") String filePath) {
        List<String> customerRecordFieldNames = CustomerRecord.getFields();
        List<String> customFieldNames = customFieldMappingService.fetchCustomFieldNamesByAccountId(accountId);
        List<String> headers = Stream.concat(customerRecordFieldNames.stream(), customFieldNames.stream()).toList();

        FlatFileItemWriter<CustomerRecord> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(filePath));
        writer.setHeaderCallback(writer1 -> writer1.write(String.join(",", headers)));

        CustomerRecordFieldExtractor fieldExtractor = new CustomerRecordFieldExtractor();
        fieldExtractor.setFieldNames(headers.toArray(String[]::new));
        fieldExtractor.setCustomFieldMappingList(customFieldMappingService.fetchCustomFieldMappingByAccountId(accountId));

        DelimitedLineAggregator<CustomerRecord> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setQuoteCharacter("'");
        lineAggregator.setFieldExtractor(fieldExtractor);

        writer.setLineAggregator(lineAggregator);
        return writer;
    }

    @Bean(name = "exportItemReader")
    @StepScope
    public ItemStreamReader<CustomerRecord> dbReader(@Value("#{jobParameters[accountId]}") Long accountId) throws Exception {
        return itemStreamReader(accountId, customerRecordMapper);
    }

    @StepScope
    public ItemStreamReader<CustomerRecord> itemStreamReader(Long accountId, RowMapper<CustomerRecord> rowMapper) throws Exception {
        JdbcPagingItemReader<CustomerRecord> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = getSqlPagingQueryProviderFactoryBean(accountId);
        reader.setQueryProvider(Objects.requireNonNull(sqlPagingQueryProviderFactoryBean.getObject()));
        reader.setPageSize(10_000);
        reader.setRowMapper(rowMapper);
        reader.afterPropertiesSet();
        reader.setSaveState(false);
        return reader;
    }

    private SqlPagingQueryProviderFactoryBean getSqlPagingQueryProviderFactoryBean(Long accountId) {
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
        sqlPagingQueryProviderFactoryBean.setSelectClause("select * ");
        sqlPagingQueryProviderFactoryBean.setFromClause("from customer_records join custom_fields on customer_records.id = custom_fields.customer_record_id");
        sqlPagingQueryProviderFactoryBean.setWhereClause("where customer_records.account_id = " + accountId);
        sqlPagingQueryProviderFactoryBean.setSortKey("email");
        return sqlPagingQueryProviderFactoryBean;
    }
}
