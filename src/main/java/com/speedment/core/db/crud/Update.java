package com.speedment.core.db.crud;

/**
 * Created by Emil on 2015-08-18.
 */
public interface Update extends Operation, Selective, Valued {

    /**
     * {@inheritDoc}
     */
    @Override
    default Type getType() {
        return Type.UPDATE;
    }
}