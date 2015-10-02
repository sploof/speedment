package com.speedment.db.crud;

import com.speedment.annotation.Api;
import java.util.Map;

/**
 *
 * @author Emil Forslund
 * @param <OPERATION>  the type of the valued operation being built
 * @param <BUILDER>    the type of the implementing builder class
 */
@Api(version = "2.2")
public interface ValuedBuilder<OPERATION extends CrudOperation & Valued, BUILDER extends ValuedBuilder<OPERATION, BUILDER>> extends CrudOperationBuilder<OPERATION> {
    
    /**
     * Sets the value for a particular column.
     *
     * @param columnName  the column
     * @param value       the value
     * @return            a reference to this builder
     */
    BUILDER with(String columnName, Object value);

    /**
     * Adds all the specified values mapped to the particular column name.
     * If the same column name already has a value, the old value will be
     * overwritten with the new one suggested.

     * @param values      values mapped to column names
     * @return            a reference to this builder
     */
    BUILDER with(Map<String, Object> values);

    /**
     * {@inheritDoc} 
     */
    @Override
    default boolean isValued() {
        return true;
    }
}