package com.focus.sv.ws.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.support.KeyHolder;

public abstract class BatchPreparedStatementSetterWithKeyHolder<T> implements BatchPreparedStatementSetter  {
	private final List<T> beans;

    public BatchPreparedStatementSetterWithKeyHolder(List<T> beans) {
        this.beans = beans;
    }


    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        setValues(ps, beans.get(i));
    }

    @Override
    public final int getBatchSize() {
        return beans.size();
    }

    public void setPrimaryKey(KeyHolder keyHolder) {
        List<Map<String, Object>> keys = keyHolder.getKeyList();
        for (int i = 0, len = keys.size(); i < len; i++) {
            setPrimaryKey(keys.get(i), beans.get(i));
        }
    }

    protected abstract void setValues(PreparedStatement ps, T bean) throws SQLException;
    protected abstract void setPrimaryKey(Map<String, Object> primaryKey, T bean);
}
