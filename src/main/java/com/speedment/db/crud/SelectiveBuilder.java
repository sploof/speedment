package com.speedment.db.crud;

/**
 *
 * @author Emil Forslund
 * @param <R> the implementing class
 */
public interface SelectiveBuilder<R extends SelectiveBuilder<R>> {
    
    /**
     * Appends an additional selector to this builder to determine which 
     * entities to apply the operation to.
     *
     * @param selector  the selector
     * @return          a reference to this builder
     */
    R where(Selector selector);

    /**
     * Limits the maximum number of entities that the operation may affect.
     *
     * @param limit  the new limit
     * @return       a reference to this builder
     */
    R limit(long limit);
}