package com.customerrecordsmanagement.config;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@AllArgsConstructor
public class JobLauncherConfig {
    private DataSource dataSource;

    @Bean(name = "batchExportJobLauncher")
    public JobLauncher batchExportJobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(batchExportJobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean(name = "batchExportJobRepository")
    public JobRepository batchExportJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(resourceLessTransactionManager());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean(name = "batchExportTransactionManager")
    public PlatformTransactionManager resourceLessTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean(name = "batchImportJobLauncher")
    public JobLauncher batchImportJobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(batchImportJobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean(name = "batchImportJobRepository")
    public JobRepository batchImportJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }
}
