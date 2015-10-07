package com.speedment.field.builders;

import com.speedment.annotation.Api;
import com.speedment.field.ReferenceForeignKeyField;
import com.speedment.field.methods.Getter;

/**
 *
 * @author Emil Forslund
 * @param <ENTITY>  the entity that called the finder
 * @param <V>       the type of the foreign key column
 * @param <FK>      the foreign entity that is referenced
 * @since 2.2
 */
@Api(version = "2.2")
public interface ForeignKeyFinderBuilder<ENTITY, V, FK> extends Getter<ENTITY, FK> {
    
    /**
     * Returns the field that was used to construct this finder builder.
     * @return  the field
     */
    ReferenceForeignKeyField<ENTITY, V, FK> getField();
}