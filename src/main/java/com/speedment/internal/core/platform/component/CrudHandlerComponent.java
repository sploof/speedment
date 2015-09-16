package com.speedment.internal.core.platform.component;

import com.speedment.annotation.Api;
import com.speedment.db.crud.Create;
import com.speedment.db.crud.Delete;
import com.speedment.db.crud.Read;
import com.speedment.db.crud.Result;
import com.speedment.db.crud.Update;
import com.speedment.exception.SpeedmentException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author Emil Forslund
 * @since 2.2
 */
@Api(version = "2.2")
public interface CrudHandlerComponent extends Component {
    
    interface CreateMethod {
        <T> T apply(Create create, Function<Result, T> mapper) throws SpeedmentException;
    }
    
    interface UpdateMethod {
        <T> T apply(Update update, Function<Result, T> mapper) throws SpeedmentException;
    }
    
    interface ReadMethod {
        <T> Stream<T> apply(Read read, Function<Result, T> mapper) throws SpeedmentException;
    }
    
    interface DeleteMethod {
        void apply(Delete delete) throws SpeedmentException;
    }
    
    @Override
    default Class<CrudHandlerComponent> getComponentClass() {
        return CrudHandlerComponent.class;
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
}