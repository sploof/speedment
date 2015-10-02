package com.speedment.db.crud;

import com.speedment.annotation.Api;

/**
 *
 * @author Emil Forslund
 */
@Api(version = "2.2")
public interface UpdateBuilder extends ValuedBuilder<Update, UpdateBuilder>, SelectiveBuilder<Update, UpdateBuilder> {}