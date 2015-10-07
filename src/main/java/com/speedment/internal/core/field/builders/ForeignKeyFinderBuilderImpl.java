package com.speedment.internal.core.field.builders;

import com.speedment.field.ReferenceForeignKeyField;
import com.speedment.field.builders.ForeignKeyFinderBuilder;
import com.speedment.field.methods.Getter;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Emil Forslund
 * @param <ENTITY>
 * @param <V>
 * @param <FK>
 */
public final class ForeignKeyFinderBuilderImpl<ENTITY, V, FK> implements 
    ForeignKeyFinderBuilder<ENTITY, V, FK> {
    
    private final ReferenceForeignKeyField<ENTITY, V, FK> field;
    private final Getter<ENTITY, FK> finder;
    
    public ForeignKeyFinderBuilderImpl(ReferenceForeignKeyField<ENTITY, V, FK> field, Getter<ENTITY, FK> finder) {
        this.field  = requireNonNull(field);
        this.finder = requireNonNull(finder);
    }

    @Override
    public ReferenceForeignKeyField<ENTITY, V, FK> getField() {
        return field;
    }

    @Override
    public FK apply(ENTITY entity) {
        return finder.apply(entity);
    }
}