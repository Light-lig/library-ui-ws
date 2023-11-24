package com.focus.sv.ws.service;

import java.util.List;
import java.util.Map;

import com.focus.sv.ws.utils.XmlQueriesUtil;

public interface JdbcTemplateService {

	public default String getQueryByName(String queryName) {
		return XmlQueriesUtil.getQueryByName(queryName);
	}	
	
	public <T> T object(String sql, Map<String, Object> params, Class<T> clazz);
	public <T> T object(String sql, Class<T> clazz, Object... params);
	public Map<String, Object> map(String sql, Map<String, Object> params);
	public Map<String, Object> map(String sql, Object... params);
	public List<Map<String, Object>> list(String sql, Map<String, Object> params);
	public List<Map<String, Object>> list(String sql, Object... params);
	public <T> List<T> listClass(String queryName, Map<String, Object> params, Class<T> clazz);
	public int update(String sql, Map<String, Object> params);
	public int update(String sql, Object... params);
	public <T> Long insert(String sql, Map<String, Object> params, String nameKey);
	public void batchUpdate(String sql, List<Map<String, Object>> data);
	public void batchInsert(final String query, List<Map<String, Object>> list, final String id);
	
	public void batchInsertByQueryName(String tableName, List<Map<String, Object>> list, String... params);
	public void insertByQueryName(String tableName, Map<String, Object> map, String...params);
	
	public void updateByQueryName(String tableName, Map<String, Object> map, String key);

	void deleteByQueryName(String tableName, Map<String, Object> map);
}
