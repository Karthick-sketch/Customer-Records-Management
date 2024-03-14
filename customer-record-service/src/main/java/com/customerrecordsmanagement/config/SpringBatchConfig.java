package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customfields.customfieldmapping.CustomFieldMappingService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
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

    @Bean
    public Job runJob() throws Exception {
        return new JobBuilder("firstBatchJob", getJobRepository())
                .flow(step()).end().build();
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
    @Async
    public Step step() throws Exception {
        return new StepBuilder("step", getJobRepository())
                .<CustomerRecord, CustomerRecord>chunk(100, transactionManager)
                .reader(dbReader())
                .processor(itemProcessor())
                .writer(fileWriter())
                .taskExecutor(taskExecutor())
                .build();
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

        writer.setHeaderCallback(writer1 -> writer1.write(String.join(", ", headers)));

        DelimitedLineAggregator<CustomerRecord> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");

        BeanWrapperFieldExtractor<CustomerRecord> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(headers.toArray(String[]::new));

        lineAggregator.setFieldExtractor(fieldExtractor);

        writer.setLineAggregator(lineAggregator);
        return writer;
    }

    @Bean
    @StepScope
    public ItemStreamReader<CustomerRecord> dbReader() throws Exception {
        return (ItemStreamReader<CustomerRecord>) itemStreamReader(customerRecordMapper);
    }

    @StepScope
    public ItemStreamReader<?> itemStreamReader(RowMapper<?> rowMapper) throws Exception {
        JdbcPagingItemReader<Object> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = getSqlPagingQueryProviderFactoryBean();
        reader.setQueryProvider(Objects.requireNonNull(sqlPagingQueryProviderFactoryBean.getObject()));
        reader.setPageSize(1_00_000);
        reader.setRowMapper((RowMapper<Object>) rowMapper);
        reader.afterPropertiesSet();
        reader.setSaveState(false);
        return reader;
    }

    private SqlPagingQueryProviderFactoryBean getSqlPagingQueryProviderFactoryBean() {
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
        sqlPagingQueryProviderFactoryBean.setSelectClause("select * ");
        sqlPagingQueryProviderFactoryBean.setFromClause("from customer_records join custom_fields customer_records.custom_field_id = custom_fields.id");
        // sqlPagingQueryProviderFactoryBean.setWhereClause(where);
        sqlPagingQueryProviderFactoryBean.setSortKey("email");
        return sqlPagingQueryProviderFactoryBean;
    }

    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(32);
        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(32);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("MultiThreaded-");
        return executor;
    }
}
