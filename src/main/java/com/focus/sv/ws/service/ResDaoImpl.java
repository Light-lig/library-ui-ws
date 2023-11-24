package com.focus.sv.ws.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class ResDaoImpl extends JdbcTemplateServiceImpl{

	@Autowired
	private JdbcTemplate resJdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate resNJdbcTemplate;

	@PostConstruct
	private void fillJdbcTemplate() {
		setJdbcTemplate(resJdbcTemplate);
		setNpjdbcTemplate(resNJdbcTemplate);
	}
}
