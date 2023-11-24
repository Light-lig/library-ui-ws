package com.focus.sv.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import com.focus.sv.ws.utils.XmlQueriesAnnotationProcessor;
import com.focus.sv.ws.utils.XmlQueriesUtil;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@SpringBootApplication
@EnableAuthorizationServer
@EnableResourceServer
@ComponentScan(basePackages = "com.focus.sv.ws")
public class LibraryUiWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryUiWsApplication.class, args);
	}

	@Bean
	XmlQueriesAnnotationProcessor useXmlQueriesAnnotationProcessor() {
		return new XmlQueriesAnnotationProcessor(XmlQueriesUtil.loadQueries("queries"));
	}
	
	@Bean
	Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.focus.sv.ws.controller")).build();
	}
}
