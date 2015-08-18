package com.speedment.core.db.crud;

import com.speedment.core.config.model.Column;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * Created by Emil on 2015-08-18.
 */
public interface Result {

    <T> Optional<T> getNullable(Column column);
    OptionalDouble getNullableAsDouble(Column column);
    OptionalLong getNullableAsLong(Column column);
    OptionalInt getNullableAsInt(Column column);
    Boolean getNullableAsBoolean(Column column);

    <T> T get(Column column);
    double getAsDouble(Column column);
    long getAsLong(Column column);
    int getAsInt(Column column);
    boolean getAsBoolean(Column column);


}