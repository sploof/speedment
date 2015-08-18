package com.speedment.core.db.crud;

/**
 * Created by Emil on 2015-08-18.
 */
public interface Read extends Operation, Selective {

    /**
     * {@inheritDoc}
     */
    @Override
    default Type getType() {
        return Type.READ;
    }

}