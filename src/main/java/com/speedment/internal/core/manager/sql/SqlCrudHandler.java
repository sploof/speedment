package com.speedment.internal.core.manager.sql;

import com.speedment.Speedment;
import com.speedment.config.Dbms;
import com.speedment.config.parameters.DbmsType;
import com.speedment.db.crud.Create;
import com.speedment.db.crud.Delete;
import com.speedment.db.crud.Read;
import com.speedment.db.crud.Result;
import com.speedment.db.crud.Update;
import com.speedment.exception.SpeedmentException;
import com.speedment.internal.core.platform.component.ConnectionPoolComponent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import com.speedment.db.crud.CrudOperation;

/**
 *
 * @author Emil
 */
public final class SqlCrudHandler extends AbstractCrudHandler {
    
    private final Dbms dbms;
    
    public SqlCrudHandler(Speedment speedment, Dbms dbms) {
        super(speedment);
        this.dbms = dbms;
    }

    @Override
    protected <T> T create(Create create, Function<Result, T> mapper) throws SpeedmentException {
        try {
            final PreparedStatement ps = SqlWriter.prepare(getConnection(), create);
            if (ps.executeUpdate() > 0) {
                return mapper.apply(new SqlResult(ps.getResultSet()));
            } else {
                throw new SpeedmentException(
                    "Insert operation did not result in any new rows."
                );
            }
        } catch (SQLException ex) {
            throw new SpeedmentException(ex);
        }
    }

    @Override
    protected <T> T update(Update update, Function<Result, T> mapper) throws SpeedmentException {
        try {
            final PreparedStatement ps = SqlWriter.prepare(getConnection(), update);
            if (ps.executeUpdate() > 0) {
                return mapper.apply(new SqlResult(ps.getResultSet()));
            } else {
                throw new SpeedmentException(
                    "Insert operation did not result in any new rows."
                );
            }
        } catch (SQLException ex) {
            throw new SpeedmentException(ex);
        }
    }

    @Override
    protected void delete(Delete delete) throws SpeedmentException {
        try {
            final PreparedStatement ps = SqlWriter.prepare(getConnection(), delete);
            ps.execute();
        } catch (SQLException ex) {
            throw new SpeedmentException(ex);
        }
    }

    @Override
    protected <T> Stream<T> read(Read read, Function<Result, T> mapper) throws SpeedmentException {
        try {
            final PreparedStatement ps = SqlWriter.prepare(getConnection(), read);
            final ResultSet rs = ps.executeQuery();
            final SqlResult sqlResult = new SqlResult(rs);

            return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                    new Iterator<T>() {
                        @Override
                        public boolean hasNext() {
                            try {
                                return rs.next();
                            } catch (SQLException ex) {
                                throw new SpeedmentException(ex);
                            }
                        }

                        @Override
                        public T next() {
                            return mapper.apply(sqlResult);
                        }
                    }, 
                    Spliterator.IMMUTABLE
                ), false
            );
        } catch (SQLException ex) {
            throw new SpeedmentException(ex);
        }
    }
    
    private Stream<Result> execute(CrudOperation operation) {
        try {
            final PreparedStatement ps = SqlWriter.prepare(getConnection(), operation);
            ps.ex
            final ResultSet rs = ps.executeQuery();
            return Stream.of(new SqlResult(rs));
        } catch (SQLException ex) {
            throw new SpeedmentException(ex);
        }
    }
    
    private Stream<Result> executeUpdate(CrudOperation operation) {
        try {
            final PreparedStatement ps = SqlWriter.prepare(getConnection(), operation);
            
            ps.
            
            final ResultSet rs = ps.executeUpdate();
            return Stream.of(new SqlResult(rs));
        } catch (SQLException ex) {
            throw new SpeedmentException(ex);
        }
    }
    
    private Stream<Result> executeQuery(CrudOperation operation) {
        try {
            final PreparedStatement ps = SqlWriter.prepare(getConnection(), operation);
            final ResultSet rs = ps.executeQuery();
            return Stream.of(new SqlResult(rs));
        } catch (SQLException ex) {
            throw new SpeedmentException(ex);
        }
    }
    
    
    
    private Connection getConnection() throws SQLException {
        final DbmsType dbmsType = dbms.getType();
        final String username   = dbms.getUsername().orElse(dbmsType.getDefaultUsername());
        final String password   = dbms.getPassword().orElse(dbmsType.getDefaultPassword());
        
        return speedment().get(ConnectionPoolComponent.class)
            .getConnection(getUri(), username, password);
    }
    
    private String getUri() {
        final DbmsType dbmsType = dbms.getType();
        final StringBuilder result = new StringBuilder();
        result.append("jdbc:");
        result.append(dbmsType.getJdbcConnectorName());
        result.append("://");
        dbms.getIpAddress().ifPresent(ip -> result.append(ip));
        dbms.getPort().ifPresent(p -> result.append(":").append(p));
        result.append("/");

        dbmsType.getDefaultConnectorParameters().ifPresent(d -> result.append("?").append(d));

        return result.toString();
    }
}