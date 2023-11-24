package com.focus.sv.ws;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;

@Configuration
public class MBeanConfig {


	@Bean
	MBeanExporter exporter() {
		final MBeanExporter exporter = new MBeanExporter();
		exporter.setAutodetect(true);
		exporter.setExcludedBeans("resDataSource");
		return exporter;
	}
	
	@Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
