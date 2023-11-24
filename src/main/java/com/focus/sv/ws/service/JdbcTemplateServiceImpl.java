package com.focus.sv.ws.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.InterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;

import com.focus.sv.ws.utils.BatchPreparedStatementSetterWithKeyHolder;

public class JdbcTemplateServiceImpl implements JdbcTemplateService {
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate npjdbcTemplate;

	
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public NamedParameterJdbcTemplate getNpjdbcTemplate() {
		return npjdbcTemplate;
	}

	public void setNpjdbcTemplate(NamedParameterJdbcTemplate npjdbcTemplate) {
		this.npjdbcTemplate = npjdbcTemplate;
	}

	public <T> T object(String sql, Map<String, Object> params, Class<T> clazz) {
		return getNpjdbcTemplate().queryForObject(sql.toString(), params, clazz);
	}

	public <T> T object(String sql, Class<T> clazz, Object... params) {
		return getJdbcTemplate().queryForObject(sql.toString(), clazz, params);
	}

	public Map<String, Object> map(String sql, Map<String, Object> params) {
		return getNpjdbcTemplate().queryForMap(sql.toString(), params);
	}

	public Map<String, Object> map(String sql, Object... params) {
		return getJdbcTemplate().queryForMap(sql.toString(), params);
	}

	public List<Map<String, Object>> list(String sql, Map<String, Object> params) {
		return getNpjdbcTemplate().queryForList(sql.toString(), params);
	}

	public List<Map<String, Object>> list(String sql, Object... params) {
		return params == null || params.length <= 0 ? getJdbcTemplate().queryForList(sql.toString())
				: getJdbcTemplate().queryForList(sql.toString(), params);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> listClass(String queryName, Map<String, Object> params, Class<T> clazz) {
		try {
			return getNpjdbcTemplate().query(getQueryByName(queryName), params, new BeanPropertyRowMapper(clazz));
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	public int update(String sql, Map<String, Object> params) {
		return getNpjdbcTemplate().update(sql.toString(), params);
	}

	public int update(String sql, Object... params) {
		return getJdbcTemplate().update(sql.toString(), params);
	}

	public <T> Long insert(String sql, Map<String, Object> params, String nameKey) {
		SqlParameterSource fileParameters = new MapSqlParameterSource(params);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getNpjdbcTemplate().update(sql, fileParameters, keyHolder, new String[] { nameKey });
		params.put(nameKey, keyHolder.getKey().longValue());
		return keyHolder.getKey().longValue();
	}

	@SuppressWarnings("unchecked")
	public void batchUpdate(String sql, List<Map<String, Object>> data) {
		getNpjdbcTemplate().batchUpdate(sql, data.toArray(new Map[data.size()]));
	}

	public void batchInsert(final String query, List<Map<String, Object>> list, final String id) {
		String[] columnsReturn = (id == null ? null : new String[] { id });
		batchUpdateWithKeyHolder(getJdbcTemplate(), query,
				new BatchPreparedStatementSetterWithKeyHolder<Map<String, Object>>(list) {
					@Override
					protected void setValues(PreparedStatement ps, Map<String, Object> map) throws SQLException {
						Object[] values = NamedParameterUtils.buildValueArray(query, map);
						setValuesPS(ps, values);
					}

					@Override
					protected void setPrimaryKey(Map<String, Object> primaryKey, Map<String, Object> map) {
						map.put(id, primaryKey.get(id));
					}
				}, columnsReturn);
	}

	private static void generatedKeys(PreparedStatement ps, KeyHolder keyHolder) throws SQLException {
		List<Map<String, Object>> keys = keyHolder.getKeyList();
		ResultSet rs = ps.getGeneratedKeys();
		if (rs == null)
			return;

		try {
			keys.addAll(
					new RowMapperResultSetExtractor<Map<String, Object>>(new ColumnMapRowMapper(), 1).extractData(rs));
		} finally {
			rs.close();
		}
	}

	private static <T> int[] batchUpdateWithKeyHolder(JdbcTemplate jdbcTemplate, final String sql,
			final BatchPreparedStatementSetterWithKeyHolder<T> pss, final String[] columnsReturn) {
		return jdbcTemplate.execute(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				if (columnsReturn == null)
					return con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				else
					return con.prepareStatement(sql, columnsReturn);
			}
		}, new PreparedStatementCallback<int[]>() {
			@Override
			public int[] doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				try {
					int batchSize = pss.getBatchSize();
					InterruptibleBatchPreparedStatementSetter ipss = (pss instanceof InterruptibleBatchPreparedStatementSetter
							? (InterruptibleBatchPreparedStatementSetter) pss
							: null);
					int[] result;
					KeyHolder keyHolder = new GeneratedKeyHolder();

					try {
						if (JdbcUtils.supportsBatchUpdates(ps.getConnection())) {
							for (int i = 0; i < batchSize; i++) {
								pss.setValues(ps, i);
								if (ipss != null && ipss.isBatchExhausted(i))
									break;
								ps.addBatch();
							}
							result = ps.executeBatch();

							generatedKeys(ps, keyHolder);
						} else {
							List<Integer> rowsAffected = new ArrayList<Integer>();
							for (int i = 0; i < batchSize; i++) {
								pss.setValues(ps, i);
								if (ipss != null && ipss.isBatchExhausted(i))
									break;

								rowsAffected.add(ps.executeUpdate());
								generatedKeys(ps, keyHolder);
							}
							result = ArrayUtils.toPrimitive(rowsAffected.toArray(new Integer[rowsAffected.size()]));
						}
					} finally {
						pss.setPrimaryKey(keyHolder);
					}

					return result;
				} finally {
					if (pss instanceof ParameterDisposer)
						((ParameterDisposer) pss).cleanupParameters();
				}
			}
		});
	}

	private static void setValuesPS(PreparedStatement ps, Object... args) throws SQLException {
		int i = 1;
		for (Object arg : args) {
			if (arg instanceof Date) {
				ps.setTimestamp(i++, new Timestamp(((Date) arg).getTime()));
			} else if (arg instanceof Integer) {
				ps.setInt(i++, (Integer) arg);
			} else if (arg instanceof Long) {
				ps.setLong(i++, (Long) arg);
			} else if (arg instanceof Double) {
				ps.setDouble(i++, (Double) arg);
			} else if (arg instanceof Float) {
				ps.setFloat(i++, (Float) arg);
			} else if (arg instanceof Float) {
				ps.setBigDecimal(i++, (BigDecimal) arg);
			} else {
				ps.setString(i++, (String) arg);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void batchInsertByQueryName(String tableName, List<Map<String, Object>> list, String... params) {
		StringBuilder sql = new StringBuilder();
		String insert = "";

		if (!list.isEmpty()) {
			sql.append("INSERT INTO " + tableName + " (");
			Map<String, Object> map = list.get(0);
			
//			for (Entry<String, Object> e : map.entrySet()) {
//				if (e.getValue() != null) {
//					sql.append(e.getKey() + ", ");
//				} else {
//					map.remove(e.getKey());
//				}
//			}
			
			Iterator<Entry<String, Object>> it = map.entrySet().iterator();
			while(it.hasNext()) {
				Entry<String, Object> item = it.next();
				if(item.getValue() != null) {
					sql.append(item.getKey() + ", ");
				} else {
					it.remove();
				}
			}

			if (params != null && params.length > 0) {
				insert = sql.replace(sql.length() - 1, sql.length(), params[0] + ") VALUES (").toString();
			} else {
				insert = sql.replace(sql.length() - 2, sql.length(), ") VALUES (").toString();
			}

			sql = new StringBuilder(insert);

			for (Entry<String, Object> e : map.entrySet()) {
				sql.append(":" + e.getKey() + ", ");

				if (e.getValue().toString().contains("|d:")) {
					try {
						String value[] = e.getValue().toString().split("\\|d:");
						map.put(e.getKey(), new SimpleDateFormat(value[1]).parse(value[0]));
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
			if (params != null && params.length > 0) {
				insert = sql.replace(sql.length() - 1, sql.length(), params[1] + ".NEXTVAL)").toString();
			} else {
				insert = sql.replace(sql.length() - 2, sql.length(), ")").toString();
			}
			sql = new StringBuilder(insert);

			Map<String, Object> m = null;
			List<Map<String, Object>> listToParse = new ArrayList<>();
			for (Map<String, Object> lm : list) {
				m = new HashMap<String, Object>();
				m.putAll(lm);
				listToParse.add(m);
			}
			Map<String, Object>[] arrayMap = listToParse.toArray(new HashMap[listToParse.size()]);
			npjdbcTemplate.batchUpdate(sql.toString(), arrayMap);
		}
	}

	@Override
	public void insertByQueryName(String tableName, Map<String, Object> map, String... params) {
		StringBuilder sql = new StringBuilder();
		String insert = "";

		if (!map.isEmpty()) {
			sql.append("INSERT INTO " + tableName + " (");
			
//			for (Entry<String, Object> e : map.entrySet()) {
//				if (e.getValue() != null) {
//					sql.append(e.getKey() + ", ");
//				} else {
//					map.remove(e.getKey());
//				}
//			}

			Iterator<Entry<String, Object>> it = map.entrySet().iterator();
			while(it.hasNext()) {
				Entry<String, Object> item = it.next();
				if(item.getValue() != null) {
					sql.append(item.getKey() + ", ");
				} else {
					it.remove();
				}
			}
			
			if (params != null && params.length > 0) {
				insert = sql.replace(sql.length() - 1, sql.length(), params[0] + ") VALUES (").toString();
			} else {
				insert = sql.replace(sql.length() - 2, sql.length(), ") VALUES (").toString();
			}

			sql = new StringBuilder(insert);

			for (Entry<String, Object> e : map.entrySet()) {

				sql.append(":" + e.getKey() + ", ");
				
				if (e.getValue().toString().contains("|d:")) {
					try {
						String value[] = e.getValue().toString().split("\\|d:");
						map.put(e.getKey(), new SimpleDateFormat(value[1]).parse(value[0]));
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
			if (params != null && params.length > 0) {
				insert = sql.replace(sql.length() - 1, sql.length(), params[1] + ".NEXTVAL)").toString();
			} else {
				insert = sql.replace(sql.length() - 2, sql.length(), ")").toString();
			}
			sql = new StringBuilder(insert);

			npjdbcTemplate.update(sql.toString(), map);
		}
	}

	@Override
	public void updateByQueryName(String tableName, Map<String, Object> map, String key) {
		StringBuilder sql = new StringBuilder();
		String update = "";

		if (!map.isEmpty()) {
			sql.append("UPDATE " + tableName + " SET ");
			
//			for (Entry<String, Object> e : map.entrySet()) {
//				if (e.getValue() != null) {
//					if (e.getKey() != key) {
//						sql.append(e.getKey() + " = :" + e.getKey() + ", ");
//					}
//					if (e.getValue().toString().contains("|d:")) {
//						try {
//							String value[] = e.getValue().toString().split("\\|d:");
//							map.put(e.getKey(), new SimpleDateFormat(value[1]).parse(value[0]));
//						} catch (Exception ex) {
//							System.out.println(ex.getMessage());
//						}
//					}
//				} else {
//					map.remove(e.getKey());
//				}
//			}

			Iterator<Entry<String, Object>> it = map.entrySet().iterator();
			while(it.hasNext()) {
				Entry<String, Object> item = it.next();
				if(item.getValue() != null) {
					if (item.getKey() != key) {
						sql.append(item.getKey() + " = :" + item.getKey() + ", ");
					}
					if (item.getValue().toString().contains("|d:")) {
						try {
							String value[] = item.getValue().toString().split("\\|d:");
							map.put(item.getKey(), new SimpleDateFormat(value[1]).parse(value[0]));
						} catch (Exception ex) {
							System.out.println(ex.getMessage());
						}
					}
				} else {
					it.remove();
				}
			}
			
			update = sql.replace(sql.length() - 2, sql.length(), " WHERE " + key + " = :" + key).toString();
			sql = new StringBuilder(update);

			npjdbcTemplate.update(sql.toString(), map);
		}
	}

	@Override
	public void deleteByQueryName(String tableName, Map<String, Object> map) {
		StringBuilder sql = new StringBuilder();
		String delete = "DELETE FROM " + tableName + " WHERE " + map.get("id") + " = :" + map.get("id");
//		map.remove("id");
		sql = new StringBuilder(delete);
		npjdbcTemplate.update(sql.toString(), map);
	}

}
