package com.speedment.core.db.crud.impl;

import com.speedment.core.config.model.Column;
import com.speedment.core.db.crud.Result;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Emil on 2015-08-18.
 */
public final class ResultImpl implements Result {

    private final Map<Column, Object> data;

    private ResultImpl(Map<Column, Object> data) {
        this.data = data;
    }

    @Override
    public <T> Optional<T> getNullable(Column column) {
        return Optional.ofNullable(get(column));
    }

    @Override
    public OptionalDouble getNullableAsDouble(Column column) {
        return OptionalDouble.of(get(column));
    }

    @Override
    public OptionalLong getNullableAsLong(Column column) {
        return OptionalLong.of(get(column));
    }

    @Override
    public OptionalInt getNullableAsInt(Column column) {
        return OptionalInt.of(get(column));
    }

    @Override
    public Boolean getNullableAsBoolean(Column column) {
        return get(column);
    }

    @Override
    public <T> T get(Column column) {
        return (T) data.get(column);
    }

    @Override
    public double getAsDouble(Column column) {
        return get(column);
    }

    @Override
    public long getAsLong(Column column) {
        return get(column);
    }

    @Override
    public int getAsInt(Column column) {
        return get(column);
    }

    @Override
    public boolean getAsBoolean(Column column) {
        return get(column);
    }

    public static class Builder {

        private final Map<Column, Object> data;

        public Builder() {
            data = new ConcurrentHashMap<>();
        }

        public <T> Builder put(Column column, T value) {
            data.put(column, value);
            return this;
        }

        public Builder put(Column column, double value) {
            data.put(column, value);
            return this;
        }

        public Builder put(Column column, long value) {
            data.put(column, value);
            return this;
        }

        public Builder put(Column column, int value) {
            data.put(column, value);
            return this;
        }

        public Builder put(Column column, boolean value) {
            data.put(column, value);
            return this;
        }

        public Result build() {
            return new ResultImpl(data);
        }
    }
}
