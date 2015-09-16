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
package com.speedment.db;

import com.speedment.annotation.Api;
import com.speedment.config.Dbms;
import com.speedment.config.Schema;
import com.speedment.db.crud.Create;
import com.speedment.db.crud.Delete;
import com.speedment.db.crud.Read;
import com.speedment.db.crud.Result;
import com.speedment.db.crud.Update;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * A DbmsHandler provides the interface between Speedment and an underlying 
 * {@link Dbms}.
 *
 * @author pemi
 * @since 2.0
 */
@Api(version = "2.1")
public interface DbmsHandler {

    /**
     * Returns the {@link Dbms} node that is used by this {@code DbmsHandler}.
     *
     * @return  the {@link Dbms} node
     */
    Dbms getDbms();

    /**
     * Returns a Stream of un-populated {@link Schema Schemas} that are 
     * available in this database. The schemas are not populated by tables, 
     * columns etc. and thus, contains only top level Schema information. 
     * Schemas that are a part of the 
     * {@code getDbms().getType().getSchemaExcludSet()} set are excluded from 
     * the {@code Stream}.
     * <p>
     * This method can be used to present a list of available Schemas before
     * they are actually being used, for example in a GUI.
     *
     * @return  a Stream of un-populated Schemas that are available in this
     *          database
     */
    Stream<Schema> schemasUnpopulated();

    /**
     * Returns a Stream of populated {@link Schema Schemas} that are available 
     * in this database. The schemas are populated by all their sub items such 
     * as tables, columns etc. Schemas that are a part of the
     * {@code getDbms().getType().getSchemaExcludSet()} set are excluded from 
     * the {@code Stream}.
     * <p>
     * This method can be used to obtain a complete inventory of the database
     * structure.
     *
     * @return  a stream of populated {@link Schema Schemas} that are available 
     *          in this database
     */
    Stream<Schema> schemas();

    /**
     * Executes the specified {@link Create} operation in the database, mapping
     * the result using the specified mapper.
     * 
     * @param <T>        the expected return type
     * @param operation  the operation to perform
     * @param mapper     for the result
     * @return           the mapped result
     */
    <T> Stream<T> executeCreate(final Create operation, final Function<Result, T> mapper);
    
    /**
     * Executes the specified {@link Read} operation in the database, mapping
     * the result using the specified mapper.
     * 
     * @param <T>        the expected return type
     * @param operation  the operation to perform
     * @param mapper     for the result
     * @return           the mapped result
     */
    <T> Stream<T> executeRead(final Read operation, final Function<Result, T> mapper);
    
    /**
     * Executes the specified {@link Update} operation in the database, mapping
     * the result using the specified mapper.
     * 
     * @param <T>        the expected return type
     * @param operation  the operation to perform
     * @param mapper     for the result
     * @return           the mapped result
     */
    <T> Stream<T> executeUpdate(final Update operation, final Function<Result, T> mapper);
    
    /**
     * Executes the specified {@link Delete} operation in the database, mapping
     * the result using the specified mapper.
     * 
     * @param <T>        the expected return type
     * @param operation  the operation to perform
     * @param mapper     for the result
     * @return           the mapped result
     */
    <T> Stream<T> executeDelete(final Delete operation, final Function<Result, T> mapper);
}