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
package com.speedment.component;

import com.speedment.annotation.Api;
import com.speedment.db.crud.Create;
import com.speedment.db.crud.Delete;
import com.speedment.db.crud.Read;
import com.speedment.db.crud.Result;
import com.speedment.db.crud.Update;
import com.speedment.exception.SpeedmentException;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This component is used to execute CRUD operations generated by Speedment.
 * For each type of CRUD operation, there is a specific executor that will
 * consume all events of that type. By setting the executor, you can control
 * how Speedment stores and reads data.
 * <p>
 * The executor should be set by the persistance manager (like the database
 * manager) before any operations are executed.
 * 
 * @author Emil Forslund
 * @since 2.2
 */
@Api(version = "2.2")
public interface CrudHandlerComponent extends Component {
    
    /**
     * Functional interface for something that can execute a {@link Create}
     * operation and map the results into T using the specified mapper.
     */
    @FunctionalInterface
    interface CreateMethod {
        <T> T apply(Create create, Function<Result, T> mapper) throws SpeedmentException;
    }
    
    /**
     * Functional interface for something that can execute an {@link Update}
     * operation and map the results into T using the specified mapper.
     */
    @FunctionalInterface
    interface UpdateMethod {
        <T> T apply(Update update, Function<Result, T> mapper) throws SpeedmentException;
    }
    
    /**
     * Functional interface for something that can execute a {@link Read}
     * operation and map the results into T using the specified mapper.
     */
    @FunctionalInterface
    interface ReadMethod {
        <T> Stream<T> apply(Read read, Function<Result, T> mapper) throws SpeedmentException;
    }
    
    /**
     * Functional interface for something that can execute a {@link Delete}
     * operation.
     */
    @FunctionalInterface
    interface DeleteMethod {
        void apply(Delete delete) throws SpeedmentException;
    }

    /**
     * Sets the method to use when 
     * {@link #create(com.speedment.db.crud.Create, java.util.function.Function)} 
     * is called.
     * 
     * @param creator  to use
     * @return         a reference to this
     */
    CrudHandlerComponent setCreator(CreateMethod creator);
    
    /**
     * Sets the method to use when 
     * {@link #update(com.speedment.db.crud.Update, java.util.function.Function)} 
     * is called.
     * 
     * @param updater  to use
     * @return         a reference to this
     */
    CrudHandlerComponent setUpdater(UpdateMethod updater);
    
    /**
     * Sets the method to use when 
     * {@link #delete(com.speedment.db.crud.Delete, java.util.function.Function)} 
     * is called.
     * 
     * @param deleter  to use
     * @return         a reference to this
     */
    CrudHandlerComponent setDeleter(DeleteMethod deleter);
    
    /**
     * Sets the method to use when 
     * {@link #executeDelete(com.speedment.db.crud.Read, java.util.function.Function)} 
     * is called.
     * 
     * @param reader  to use
     * @return        a reference to this
     */
    CrudHandlerComponent setReader(ReadMethod reader);
    
    /**
     * Executes the specified {@link Create} operation in the database, mapping
     * the result using the specified mapper.
     * 
     * @param <T>                  the expected return type
     * @param operation            the operation to perform
     * @param mapper               for the result
     * @return                     the mapped result
     * @throws SpeedmentException  if the operation failed
     */
    <T> T create(final Create operation, final Function<Result, T> mapper) throws SpeedmentException;
    
    /**
     * Executes the specified {@link Update} operation in the database, mapping
     * the result using the specified mapper.
     * 
     * @param <T>                  the expected return type
     * @param operation            the operation to perform
     * @param mapper               for the result
     * @return                     the mapped result
     * @throws SpeedmentException  if the operation failed
     */
    <T> T update(final Update operation, final Function<Result, T> mapper) throws SpeedmentException;
    
    /**
     * Executes the specified {@link Delete} operation in the database, mapping
     * the result using the specified mapper.
     * 
     * @param <T>                  the expected return type
     * @param operation            the operation to perform
     * @throws SpeedmentException  if the operation failed
     */
    <T> void delete(final Delete operation) throws SpeedmentException;
    
    /**
     * Executes the specified {@link Read} operation in the database, mapping
     * the result using the specified mapper.
     * 
     * @param <T>                  the expected return type
     * @param operation            the operation to perform
     * @param mapper               for the result
     * @return                     the mapped result
     * @throws SpeedmentException  if the operation failed
     */
    <T> Stream<T> read(final Read operation, final Function<Result, T> mapper) throws SpeedmentException;
    
    /**
     * {@inheritDoc}
     */
    @Override
    default Class<CrudHandlerComponent> getComponentClass() {
        return CrudHandlerComponent.class;
    }
}