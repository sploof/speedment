package com.speedment.core.db.crud;

import com.speedment.core.config.model.Column;

import java.util.Map;

/**
 * Created by Emil on 2015-08-18.
 */
public interface Valued {

    /**
     * Returns the data passed in this operation mapped to the particular columns.
     *
     * @return  the values
     */
    Map<Column, Object> getValues();
}
