package com.speedment.core.db.impl;

import com.speedment.core.config.model.Column;
import com.speedment.core.db.crud.Result;
import com.speedment.core.exception.SpeedmentException;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

/**
 * Created by Emil on 2015-08-18.
 */
public class SqlResult implements Result {

    private final ResultSet result;

    public SqlResult(ResultSet result) {
        this.result = result;
    }

    @Override
    public Array getArray(Column column) {
        return get(result::getArray, column);
    }

    @Override
    public BigDecimal getBigDecimal(Column column) {
        return get(result::getBigDecimal, column);
    }

    @Override
    public Blob getBlob(Column column) {
        return get(result::getBlob, column);
    }

    @Override
    public Clob getClob(Column column) {
        return get(result::getClob, column);
    }

    @Override
    public NClob getNClob(Column column) {
        return get(result::getNClob, column);
    }

    @Override
    public Ref getRef(Column column) {
        return get(result::getRef, column);
    }

    @Override
    public Object getObject(Column column) {
        return get(result::getObject, column);
    }

    @Override
    public double getDouble(Column column) {
        return get(result::getDouble, column);
    }

    @Override
    public long getLong(Column column) {
        return get(result::getLong, column);
    }

    @Override
    public float getFloat(Column column) {
        return get(result::getFloat, column);
    }

    @Override
    public int getInt(Column column) {
        return get(result::getInt, column);
    }

    @Override
    public short getShort(Column column) {
        return get(result::getShort, column);
    }

    @Override
    public byte getByte(Column column) {
        return get(result::getByte, column);
    }

    @Override
    public boolean getBoolean(Column column) {
        return get(result::getBoolean, column);
    }

    @Override
    public String getString(Column column) {
        return get(result::getString, column);
    }

    @Override
    public URL getURL(Column column) {
        return get(result::getURL, column);
    }

    @Override
    public SQLXML getSQLXML(Column column) {
        return get(result::getSQLXML, column);
    }

    @Override
    public Timestamp getTimestamp(Column column) {
        return get(result::getTimestamp, column);
    }

    @Override
    public Time getTime(Column column) {
        return get(result::getTime, column);
    }

    @Override
    public Date getDate(Column column) {
        return get(result::getDate, column);
    }

    private static <T> T get(SqlGetter<T> getter, Column column) {
        try {
            return getter.apply(columnName(column));
        } catch (SQLException ex) {
            throw new SpeedmentException("Error mapping ResultSet to SqlResult on column " + column.getName() + ".", ex);
        }
    }

    private static String columnName(Column column) {
        return column.getName();
    }

    @FunctionalInterface
    private interface SqlGetter<T> {
        T apply(String columnName) throws SQLException;
    }
}
