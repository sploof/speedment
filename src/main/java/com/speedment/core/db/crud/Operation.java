package com.speedment.core.db.crud;

import com.speedment.core.config.model.Table;

/**
 * Created by Emil on 2015-08-18.
 */
public interface Operation {

    /**
     * The type of this CRUD operation.
     */
    enum Type { CREATE, READ, UPDATE, DELETE }

    /**
     * Returns the type of this CRUD operation.
     *
     * @return  the type
     */
    Type getType();

    /**
     * Returns the {@link Table} that this operation should operate on.
     *
     * @return  the table
     */
    Table getTable();
}
