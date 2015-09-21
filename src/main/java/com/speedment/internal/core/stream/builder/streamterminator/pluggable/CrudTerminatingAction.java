package com.speedment.internal.core.stream.builder.streamterminator.pluggable;

import com.speedment.config.Table;
import com.speedment.db.crud.CrudOperation;

/**
 *
 * @author Emil
 */
public interface CrudTerminatingAction extends TerminatingAction {
    
    /**
     * Returns the {@link Table} that this stream terminator will execute a 
     * {@link CrudOperation} on.
     * 
     * @return  the table
     */
    Table getTable();
    
    /**
     * Returns the type of the {@link CrudOperation} that this terminator will
     * execute.
     * 
     * @return  the crud operator type
     */
    CrudOperation.Type getType();
    
}