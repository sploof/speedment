package com.speedment.core.db.crud;

import com.speedment.core.config.model.Column;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

/**
 * Created by Emil on 2015-08-18.
 */
public interface Result {

    Array getArray(Column column);
    BigDecimal getBigDecimal(Column column);
    Blob getBlob(Column column);
    Clob getClob(Column column);
    NClob getNClob(Column column);
    Ref getRef(Column column);
    Object getObject(Column column);
    double getDouble(Column column);
    long getLong(Column column);
    float getFloat(Column column);
    int getInt(Column column);
    short getShort(Column column);
    byte getByte(Column column);
    boolean getBoolean(Column column);
    String getString(Column column);
    URL getURL(Column column);
    SQLXML getSQLXML(Column column);
    Timestamp getTimestamp(Column column);
    Time getTime(Column column);
    Date getDate(Column column);

}