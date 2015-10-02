package com.speedment.db.crud;

import com.speedment.annotation.Api;

/**
 *
 * @author Emil Forslund
 * @param <OPERATION> the type of the selective operation being built
 * @param <BUILDER>   the type of the implementing builder class
 */
@Api(version = "2.2")
public interface JoinableBuilder<OPERATION extends CrudOperation & Selective, BUILDER extends SelectiveBuilder<OPERATION, BUILDER>> extends CrudOperationBuilder<OPERATION> {
    
    /**
     * Appends an additional join to this builder to determine which 
     * entities are involved in the operation.
     *
     * @param join  the join
     * @return      a reference to this builder
     */
    BUILDER join(Join join);
    
    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isJoinable() {
        return true;
    }
}