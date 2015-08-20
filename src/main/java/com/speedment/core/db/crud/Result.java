/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.core.db.crud;

import com.speedment.core.config.model.Column;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

/**
 * @author Emil Forslund
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