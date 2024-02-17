package com.karthick.customerrecordsmanagement.customerrecords;

//import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
//import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

//import javax.sql.DataSource;

@Configuration
@Profile("batch")
public class DatasourceProxyConfiguration {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public DatasourceProxyBeanPostProcessor datasourceProxyBeanPostProcessor() {
        return new DatasourceProxyBeanPostProcessor();
    }

/*
    @Bean
    @Profile("batch")
    public DataSource realDataSource(DataSource dataSource) {
        return ProxyDataSourceBuilder
                .create(dataSource)
                .logQueryBySlf4j(SLF4JLogLevel.INFO)
                .countQuery()
                .build();
    }
*/
}
