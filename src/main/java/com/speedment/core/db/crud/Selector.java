package com.speedment.core.db.crud;

import com.speedment.core.config.model.Column;
import com.speedment.core.field.Operator;

/**
 * Created by Emil on 2015-08-18.
 */
public interface Selector {

    /**
     * Returns the column described by this selector.
     *
     * @return  the column
     */
    Column getColumn();

    /**
     * Returns the operator used in this selector.
     *
     * @return  the operator
     */
    Operator getOperator();

    /**
     * Returns the operand (if any) that this selector compares with. If the operator specified by
     * {@link #getOperator()} does not require any operands, this will be {@code null}.
     *
     * @return  the operand if any and {@code null} otherwise
     */
    Object getOperand();

}