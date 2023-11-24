package com.focus.sv.ws;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JdbcTemplateConfig {
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.res")
	DataSource resDataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name="resJdbcTemplate")
	JdbcTemplate resJdbcTemplate(@Qualifier("resDataSource") DataSource datasource) {
		return new JdbcTemplate(datasource);
	}
	
	@Bean(name="resNJdbcTemplate")
	NamedParameterJdbcTemplate resNJdbcTemplate(@Qualifier("resDataSource") DataSource datasource) {
		return new NamedParameterJdbcTemplate(datasource);
	}
	
	  @Bean(name="transactionManager")
	   public PlatformTransactionManager transactionManager() {
	      DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
	      transactionManager.setDataSource(resDataSource());
	      return transactionManager;
	   }
	
}
