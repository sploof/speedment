package com.speedment.db.crud;

import com.speedment.annotation.Api;

/**
 *
 * @author Emil Forslund
 */
@Api(version = "2.2")
public interface ReadBuilder extends SelectiveBuilder<Read, ReadBuilder>, JoinableBuilder<Read, ReadBuilder> {}