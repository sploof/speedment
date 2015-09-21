package com.speedment.db.crud;

import java.util.Map;

/**
 *
 * @author Emil Forslund
 * @param <R> the implementing class
 */
public interface ValuedBuilder<R extends ValuedBuilder<R>> {
    
    /**
     * Sets the value for a particular column.
     *
     * @param columnName  the column
     * @param value       the value
     * @return            a reference to this builder
     */
    R with(String columnName, Object value);

    /**
     * Adds all the specified values mapped to the particular column name.
     * If the same column name already has a value, the old value will be
     * overwritten with the new one suggested.

     * @param values      values mapped to column names
     * @return            a reference to this builder
     */
    R with(Map<String, Object> values);
}