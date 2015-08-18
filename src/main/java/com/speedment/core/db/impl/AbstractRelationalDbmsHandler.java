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
package com.speedment.core.db.impl;

import com.speedment.core.config.model.*;
import com.speedment.core.config.model.parameters.DbmsType;
import com.speedment.core.config.model.parameters.OrderType;
import com.speedment.core.db.DbmsHandler;
import com.speedment.core.db.crud.*;
import com.speedment.core.exception.SpeedmentException;
import com.speedment.core.platform.Platform;
import com.speedment.core.platform.component.SqlTypeMapperComponent;
import com.speedment.logging.Logger;
import com.speedment.logging.LoggerManager;
import com.speedment.util.java.sql.TypeInfo;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 *
 * @author pemi
 * @author Emil Forslund
 */
public abstract class AbstractRelationalDbmsHandler implements DbmsHandler {

    private static final Logger LOGGER = LoggerManager.getLogger(AbstractRelationalDbmsHandler.class);

    private static final String PASSWORD = "password";
    private static final String PASSWORD_PROTECTED = "********";
    private static final String USER = "user";

    private final Dbms dbms;
    private transient Map<String, Class<?>> typeMapping;

    private static final Boolean SHOW_METADATA = false;

    public AbstractRelationalDbmsHandler(Dbms dbms) {
        this.dbms = dbms;
        this.typeMapping = new ConcurrentHashMap<>();
    }

    @Override
    public Dbms getDbms() {
        return dbms;
    }

    @Override
    public <T> Stream<T> executeCreate(Create operation, Function<Result, T> mapper) {
        return executeQuery(
            SqlWriter.create(operation),
            SqlWriter.values(operation),
            rs -> mapper.apply(new SqlResult(rs))
        );
    }

    @Override
    public <T> Stream<T> executeRead(Read operation, Function<Result, T> mapper) {
        return executeQuery(
            SqlWriter.read(operation),
            Collections.emptyList(),
            rs -> mapper.apply(new SqlResult(rs))
        );
    }

    @Override
    public <T> Stream<T> executeUpdate(Update operation, Function<Result, T> mapper) {
        return executeQuery(
            SqlWriter.update(operation),
            SqlWriter.values(operation),
            rs -> mapper.apply(new SqlResult(rs))
        );
    }

    @Override
    public <T> Stream<T> executeDelete(Delete operation, Function<Result, T> mapper) {
        return executeQuery(
            SqlWriter.delete(operation),
            Collections.emptyList(),
            rs -> mapper.apply(new SqlResult(rs))
        );
    }

    @Override
    public Stream<Schema> schemas() {
        try {
            try (final Connection connection = getConnection()) {
                final List<Schema> schemas = schemas(connection).collect(toList());
                schemas.forEach(schema -> {
                    final List<Table> tables = tables(connection, schema).collect(toList());
                    tables.forEach(table -> {

                        columns(connection, schema, table).forEachOrdered(table::add);
                        primaryKeyColumns(connection, schema, table).forEachOrdered(table::add);
                        indexes(connection, schema, table).forEachOrdered(table::add);
                        foreignKeys(connection, schema, table).forEachOrdered(table::add);

                        schema.add(table);
                    });
                });
                return schemas.stream();
            }
        } catch (SQLException sqle) {
            LOGGER.error("Unable to read from " + dbms.toString(), sqle);
            return Stream.empty();
        }
    }

    @Override
    public Stream<Schema> schemasUnpopulated() {
        try {
            try (Connection connection = getConnection()) {
                return schemas(connection);
            }
        } catch (SQLException sqle) {
            LOGGER.error("Unable to read from " + dbms.toString(), sqle);
            return Stream.empty();
        }
    }

    // Todo: Use DataSoruce instead: http://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html
    public Connection getConnection() {
        Connection conn;
        final Properties connectionProps = new Properties();
        dbms.getUsername().ifPresent(u -> connectionProps.put(USER, u));
        dbms.getPassword().ifPresent(p -> connectionProps.put(PASSWORD, p));
        final String url = getUrl();
        try {
            conn = DriverManager.getConnection(url, connectionProps);
        } catch (SQLException sqle) {
            final Properties pwProtectedProperties = new Properties();
            connectionProps.forEach((k, v) -> pwProtectedProperties.put(k, v));
            pwProtectedProperties.put(PASSWORD, PASSWORD_PROTECTED);
            final String msg = "Unable to get connection for " + dbms + " using url \"" + url + "\" and connectionProperties " + pwProtectedProperties;
            LOGGER.error(msg, sqle);
            throw new SpeedmentException(msg, sqle);
        }
        return conn;
    }

    public String getUrl() {
        final DbmsType dbmsType = getDbms().getType();
        final StringBuilder result = new StringBuilder();
        result.append("jdbc:");
        result.append(dbmsType.getJdbcConnectorName());
        result.append("://");
        getDbms().getIpAddress().ifPresent(ip -> result.append(ip));
        getDbms().getPort().ifPresent(p -> result.append(":").append(p));
        result.append("/");

        dbmsType.getDefaultConnectorParameters().ifPresent(d -> result.append("?").append(d));

        return result.toString();
    }

    protected Map<String, Class<?>> readTypeMapFromDB(final Connection connection) throws SQLException {
        final Map<String, Class<?>> result = new ConcurrentHashMap<>();
        try (final ResultSet rs = connection.getMetaData().getTypeInfo()) {
            while (rs.next()) {
                final TypeInfo typeInfo = TypeInfo.from(rs);
                final Class<?> mappedClass = Platform.get().get(SqlTypeMapperComponent.class).apply(dbms, typeInfo);
                result.put(typeInfo.getSqlTypeName(), mappedClass);
            }
        }
        return result;
    }

    protected Stream<Schema> schemas(final Connection connection) {
        LOGGER.info("Reading metadata from " + dbms.toString());
        final List<Schema> schemas = new ArrayList<>();
        try {
            typeMapping = readTypeMapFromDB(connection);
            try (final ResultSet rs = connection.getMetaData().getSchemas(null, null)) {
                while (rs.next()) {
                    final String schemaName = rs.getString("TABLE_SCHEMA");
                    String catalogName = "";
                    try {
                        // This column is not there for Oracle so handle it
                        // gracefully....
                        catalogName = rs.getString("TABLE_CATALOG");
                    } catch (SQLException sqlException) {
                        LOGGER.info("TABLE_CATALOG not in result set.");
                    }
                    if (!dbms.getType().getSchemaExcludSet().contains(schemaName)) {
                        final Schema schema = Schema.newSchema();
                        schema.setName(schemaName);
                        schema.setSchemaName(schemaName);
                        schema.setCatalogName(catalogName);
                        schemas.add(schema);
                    }
                }
            }

            try (final ResultSet catalogResultSet = connection.getMetaData().getCatalogs()) {
                while (catalogResultSet.next()) {
                    final String schemaName = catalogResultSet.getString(1);
                    if (!dbms.getType().getSchemaExcludSet().contains(schemaName)) {
                        final Schema schema = Schema.newSchema();
                        schema.setName(schemaName);
                        schemas.add(schema);
                    }
                }
            }

        } catch (SQLException sqle) {
            throw new SpeedmentException(sqle);
        }
        return schemas.stream();
    }

    protected Stream<Table> tables(final Connection connection, final Schema schema) {
        final List<Table> tables = new ArrayList<>();
        LOGGER.info("Parsing " + schema.toString());
        try {
            try (final ResultSet rsTable = connection.getMetaData().getTables(jdbcCatalogLookupName(schema), jdbcSchemaLookupName(schema), null, new String[]{"TABLE"})) {

                final ResultSetMetaData rsmd = rsTable.getMetaData();
                int numberOfColumns = rsmd.getColumnCount();

                if (SHOW_METADATA) {
                    for (int x = 1; x <= numberOfColumns; x++) {
                        LOGGER.debug(rsmd.getColumnName(x) + ", " + rsmd.getColumnClassName(x) + ", " + rsmd.getColumnType(x));
                    }
                }

                // Monitoring the progress of Subtask 1. creating tables
                while (rsTable.next()) {
                    if (SHOW_METADATA) {
                        for (int x = 1; x <= numberOfColumns; x++) {
                            LOGGER.debug(rsmd.getColumnName(x) + ":'" + rsTable.getObject(x) + "'");
                        }
                    }
                    final Table table = Table.newTable();
                    final String tableName = rsTable.getString("TABLE_NAME");
                    table.setName(tableName);
                    tables.add(table);
                }
            }
        } catch (SQLException sqle) {
            throw new SpeedmentException(sqle);
        }
        return tables.stream();
    }

    protected Stream<Column> columns(final Connection connection, final Schema schema, final Table table) {
        final SqlSupplier<ResultSet> supplier = ()
            -> connection.getMetaData().getColumns(jdbcCatalogLookupName(schema), jdbcSchemaLookupName(schema), table.getName(), null);

        final SqlFunction<ResultSet, Column> mapper = rs -> {
            final Column column = Column.newColumn();
            column.setName(rs.getString("COLUMN_NAME"));
            column.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));

            boolean nullable = rs.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls;
            column.setNullable(nullable);

            final String classMappingString = rs.getString("TYPE_NAME");
            final Class<?> mapping = typeMapping.get(classMappingString);
            if (mapping != null) {
                column.setMapping(mapping);
            } else {
                LOGGER.info("Unable to determine mapping for table " + table.getName() + ", column " + column.getName());
            }

            try {
                column.setAutoincrement(rs.getBoolean("IS_AUTOINCREMENT"));
            } catch (final SQLException sqle) {
                LOGGER.info("Unable to determine IS_AUTOINCREMENT for table " + table.getName() + ", column " + column.getName());
            }
            return column;
        };
        return tableChilds(supplier, mapper);
    }

    protected Stream<PrimaryKeyColumn> primaryKeyColumns(final Connection connection, final Schema schema, final Table table) {
        final SqlSupplier<ResultSet> supplier = ()
            -> connection.getMetaData().getPrimaryKeys(jdbcCatalogLookupName(schema), jdbcSchemaLookupName(schema), table.getName());

        final SqlFunction<ResultSet, PrimaryKeyColumn> mapper = rs -> {
            final PrimaryKeyColumn primaryKeyColumn = PrimaryKeyColumn.newPrimaryKeyColumn();
            primaryKeyColumn.setName(rs.getString("COLUMN_NAME"));
            primaryKeyColumn.setOrdinalPosition(rs.getInt("KEY_SEQ"));
            return primaryKeyColumn;
        };
        return tableChilds(supplier, mapper);
    }

    protected Stream<Index> indexes(final Connection connection, final Schema schema, final Table table) {
        final Map<String, Index> indexes = new HashMap<>(); // Use map instead of Set because Index equality is difficult...
        final SqlSupplier<ResultSet> supplier = ()
            -> connection.getMetaData().getIndexInfo(jdbcCatalogLookupName(schema), jdbcSchemaLookupName(schema), table.getName(), false, false);

        final SqlFunction<ResultSet, Index> mapper = rs -> {
            final String indexName = rs.getString("INDEX_NAME");
            final boolean notUnique = rs.getBoolean("NON_UNIQUE");
            final boolean exists = indexes.containsKey(indexName);
            final Index index = indexes.computeIfAbsent(indexName, n -> {
                final Index newIndex = Index.newIndex();
                newIndex.setName(n);
                newIndex.setUnique(!notUnique); // !
                return newIndex;
            });

            final IndexColumn indexColumn = IndexColumn.newIndexColumn();
            indexColumn.setName(rs.getString("COLUMN_NAME"));
            indexColumn.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
            final String ascOrDesc = rs.getString("ASC_OR_DESC");

            if ("A".equalsIgnoreCase(ascOrDesc)) {
                indexColumn.setOrderType(OrderType.ASC);
            } else if ("D".equalsIgnoreCase(ascOrDesc)) {
                indexColumn.setOrderType(OrderType.DESC);
            } else {
                indexColumn.setOrderType(OrderType.NONE);
            }
            index.add(indexColumn);

            return exists ? null : index;
        };
        return tableChilds(supplier, mapper);
    }

    protected Stream<ForeignKey> foreignKeys(final Connection connection, final Schema schema, final Table table) {
        final Map<String, ForeignKey> foreignKeys = new HashMap<>(); // Use map instead of Set because ForeignKey equality is difficult...
        final SqlSupplier<ResultSet> supplier = () -> {
            return connection.getMetaData().getImportedKeys(jdbcCatalogLookupName(schema), jdbcSchemaLookupName(schema), table.getName());
        };
        final SqlFunction<ResultSet, ForeignKey> mapper = rs -> {
            final String foreignKeyName = rs.getString("FK_NAME");
            final boolean exists = foreignKeys.containsKey(foreignKeyName);
            final ForeignKey foreignKey = foreignKeys.computeIfAbsent(foreignKeyName, n -> {
                final ForeignKey newforeigKey = ForeignKey.newForeignKey();
                newforeigKey.setName(n);
                return newforeigKey;
            });

            final ForeignKeyColumn foreignKeyColumn = ForeignKeyColumn.newForeignKeyColumn();
            foreignKeyColumn.setName(rs.getString("FKCOLUMN_NAME"));
            foreignKeyColumn.setOrdinalPosition(rs.getInt("KEY_SEQ"));
            foreignKeyColumn.setForeignTableName(rs.getString("PKTABLE_NAME"));
            foreignKeyColumn.setForeignColumnName(rs.getString("PKCOLUMN_NAME"));
            foreignKey.add(foreignKeyColumn);

            return exists ? null : foreignKey;
        };
        return tableChilds(supplier, mapper);
    }

    protected <T> Stream<T> tableChilds(final SqlSupplier<ResultSet> resultSetSupplier, final SqlFunction<ResultSet, T> resultSetMapper) {
        final List<T> childs = new ArrayList<>();
        try {
            try (final ResultSet rsColumn = resultSetSupplier.get()) {

                final ResultSetMetaData rsmd = rsColumn.getMetaData();
                int numberOfColumns = rsmd.getColumnCount();
                if (SHOW_METADATA) {
                    for (int x = 1; x <= numberOfColumns; x++) {
                        LOGGER.debug(rsmd.getColumnName(x) + ", " + rsmd.getColumnClassName(x) + ", " + rsmd.getColumnType(x));
                    }
                }

                while (rsColumn.next()) {
                    if (SHOW_METADATA) {
                        for (int x = 1; x <= numberOfColumns; x++) {
                            LOGGER.debug(rsmd.getColumnName(x) + ":'" + rsColumn.getObject(x) + "'");
                        }
                    }
                    T child = resultSetMapper.apply(rsColumn);
                    // Null can be used to signal that the child has alredy been added (for ForeignKey and Index)
                    if (child != null) {
                        childs.add(child);
                    }
                }
            }
        } catch (SQLException sqle) {
            throw new SpeedmentException(sqle);
        }
        return childs.stream();
    }

    protected String jdbcSchemaLookupName(final Schema schema) {
        return null;
    }

    protected String jdbcCatalogLookupName(final Schema schema) {
        return schema.getSchemaName().orElse(null);
    }

    protected <T> Stream<T> executeQuery(final String sql, final List<?> values, final SqlFunction<ResultSet, T> rsMapper) {
        try (final Connection connection = getConnection(); final PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            for (final Object o : values) {
                ps.setObject(i++, o);
            }
            final ResultSet rs = ps.executeQuery();

            // Todo: Make a transparent stream with closeHandler added.
            final Stream.Builder<T> streamBuilder = Stream.builder();
            while (rs.next()) {
                streamBuilder.add(rsMapper.apply(rs));
            }
            return streamBuilder.build();
        } catch (SQLException sqle) {
            LOGGER.error("Error querying " + sql, sqle);
            throw new SpeedmentException(sqle);
        }
    }
}
