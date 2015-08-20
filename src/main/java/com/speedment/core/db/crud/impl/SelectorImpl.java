/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.core.db.crud.impl;

import com.speedment.core.config.model.Column;
import com.speedment.core.db.crud.Selector;
import com.speedment.core.field.*;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation of the {@link Selector} interface.
 *
 * @author Emil
 */
public final class SelectorImpl implements Selector {

    private final Column column;
    private final Operator operator;
    private final Object operand;

    /**
     * SelectorImpl should be constructed using the appropriate static class.
     *
     * @param column    the column to compare
     * @param operator  the operator to use when comparing
     * @param operand   the operand to compare to, or {@code null} if the operator is unary
     */
    private SelectorImpl(Column column, Operator operator, Object operand) {
        this.column   = requireNonNull(column);
        this.operator = requireNonNull(operator);
        this.operand  = operand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Column getColumn() {
        return column;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Operator getOperator() {
        return operator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Object> getOperand() {
        return Optional.ofNullable(operand);
    }

    /**
     * Constructs a new {@link Selector} based on a binary predicate and a value to test it against.
     *
     * @param predicate  the predicate
     * @param value      the value to test against
     * @param <ENTITY>   the type of the entity
     * @param <V>        the type of the value
     * @return           the constructed {@link Selector}
     * @see              BinaryPredicateBuilder
     */
    public static <ENTITY, V extends Comparable<V>> Selector fromBinaryPredicate(BinaryPredicateBuilder<ENTITY, V> predicate, V value) {
        return new SelectorImpl(
            predicate.getField().getColumn(),
            predicate.getOperator(),
            value
        );
    }

    /**
     * Constructs a new {@link Selector} based on a unary predicate.
     *
     * @param predicate  the predicate
     * @param <ENTITY>   the type of the entity
     * @return           the constructed {@link Selector}
     * @see              UnaryPredicateBuilder
     */
    public static <ENTITY> Selector fromUnaryPredicate(UnaryPredicateBuilder<ENTITY> predicate) {
        return new SelectorImpl(
            predicate.getField().getColumn(),
            predicate.getOperator(),
            null
        );
    }

    /**
     * Constructs a standard key-value selector.
     * @param key    the key
     * @param value  the expected value
     * @return       a selector for that match
     */
    public static Selector standard(Column key, Object value) {
        return new SelectorImpl(key, StandardBinaryOperator.EQUAL, value);
    }
}
