package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
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
@AllArgsConstructor
public class SpringBatchConfig {
    private DataSource dataSource;
    private PlatformTransactionManager transactionManager;
    private CustomerRecordMapper customerRecordMapper;
    private CustomFieldMappingService customFieldMappingService;

    @Bean(name = "customerRecordJob")
    public Job job() throws Exception {
        return new JobBuilder("customerRecordJob", getJobRepository())
                .start(step()).build();
    }

    @Bean
    @Async
    public Step step() throws Exception {
        return new StepBuilder("customerRecordStep", getJobRepository())
                .<CustomerRecord, CustomerRecord>chunk(100, transactionManager)
                .reader(dbReader())
                .processor(itemProcessor())
                .writer(fileWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean(name = "jobLauncher")
    public JobLauncher getJobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(getJobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    public JobRepository getJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(getTransactionManager());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    public PlatformTransactionManager getTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(4);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("MultiThreaded-");
        return executor;
    }

    @Bean
    public CustomerRecordItemProcessor itemProcessor() {
        return new CustomerRecordItemProcessor();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<CustomerRecord> fileWriter() {
        List<String> customerRecordFieldNames = CustomerRecord.getFields();
        List<String> customFieldNames = customFieldMappingService.fetchCustomFieldNamesByAccountId(1);
        List<String> headers = Stream.concat(customerRecordFieldNames.stream(), customFieldNames.stream()).toList();

        FlatFileItemWriter<CustomerRecord> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("data/output.csv"));
        writer.setHeaderCallback(writer1 -> writer1.write(String.join(",", headers)));

        BeanWrapperFieldExtractor<CustomerRecord> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(headers.toArray(String[]::new));

        CustomRecordDelimitedLineAggregator<CustomerRecord> lineAggregator = new CustomRecordDelimitedLineAggregator<>();
        lineAggregator.setFieldExtractor(fieldExtractor);
        lineAggregator.setCustomFieldNames(customFieldMappingService.fetchFieldNamesByAccountId(1));

        writer.setLineAggregator(lineAggregator);
        return writer;
    }

    @Bean
    @StepScope
    public ItemStreamReader<CustomerRecord> dbReader() throws Exception {
        return itemStreamReader(customerRecordMapper);
    }

    @Bean
    @StepScope
    public ItemStreamReader<CustomerRecord> itemStreamReader(RowMapper<CustomerRecord> rowMapper) throws Exception {
        JdbcPagingItemReader<CustomerRecord> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = getSqlPagingQueryProviderFactoryBean();
        reader.setQueryProvider(Objects.requireNonNull(sqlPagingQueryProviderFactoryBean.getObject()));
        reader.setPageSize(10_000);
        reader.setRowMapper(rowMapper);
        reader.afterPropertiesSet();
        reader.setSaveState(false);
        return reader;
    }

    private SqlPagingQueryProviderFactoryBean getSqlPagingQueryProviderFactoryBean() {
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
        sqlPagingQueryProviderFactoryBean.setSelectClause("select * ");
        sqlPagingQueryProviderFactoryBean.setFromClause("from customer_records join custom_fields on customer_records.id = custom_fields.customer_record_id");
        sqlPagingQueryProviderFactoryBean.setSortKey("email");
        return sqlPagingQueryProviderFactoryBean;
    }
}
