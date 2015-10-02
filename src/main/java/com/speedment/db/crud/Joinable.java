package com.speedment.db.crud;

import com.speedment.annotation.Api;
import java.util.stream.Stream;

/**
 *
 * @author Emil Forslund
 */
@Api(version = "2.2")
public interface Joinable {
    
    /**
     * Streams all joins that describe which entity types are involed in this
     * operation.
     *
     * @return  the joins of this operation
     */
    Stream<Join> getJoins();
}
