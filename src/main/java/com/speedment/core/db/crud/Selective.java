package com.speedment.core.db.crud;

import java.util.stream.Stream;

/**
 * Created by Emil on 2015-08-18.
 */
public interface Selective {

    /**
     * Streams all selectors that describe which entities this operation should affect.
     *
     * @return  the selectors of this operation
     */
    Stream<Selector> getSelectors();

}