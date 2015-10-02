package com.speedment.db.crud;

import com.speedment.annotation.Api;
import com.speedment.config.Table;

/**
 *
 * @author Emil
 */
@Api(version = "2.2")
public interface Join {
    
    /**
     * Returns the column name of the column in this table that should be
     * compared in the join statement.
     * 
     * @return  the column name in this table
     */
    String getColumnName();
    
    /**
     * Returns the column name of the column in the joined table. 
     * 
     * @return  the other column name
     */
    String getOtherColumnName();
    
    /**
     * Returns the table that this operation should join with.
     * 
     * @return  the other table in the join
     */
    Table getOtherTable();
}
