package com.speedment.db.crud;

import com.speedment.annotation.Api;

/**
 *
 * @author Emil Forslund
 * @param <OPERATION> the type of the selective operation being built
 * @param <BUILDER>   the type of the implementing builder class
 */
@Api(version = "2.2")
public interface SelectiveBuilder<OPERATION extends CrudOperation & Selective, BUILDER extends SelectiveBuilder<OPERATION, BUILDER>> extends CrudOperationBuilder<OPERATION> {
    
    /**
     * Appends an additional selector to this builder to determine which 
     * entities to apply the operation to.
     *
     * @param selector  the selector
     * @return          a reference to this builder
     */
    BUILDER where(Selector selector);

    /**
     * Limits the maximum number of entities that the operation may affect.
     *
     * @param limit  the new limit
     * @return       a reference to this builder
     */
    BUILDER limit(long limit);
    
    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isSelective() {
        return true;
    }
}