package com.speedment.core.db.crud;

import com.speedment.core.config.model.Column;
import com.speedment.core.config.model.Table;
import com.speedment.core.exception.SpeedmentException;
import com.speedment.core.field.Operator;
import com.speedment.core.field.StandardBinaryOperator;
import com.speedment.core.field.StandardStringBinaryOperator;
import com.speedment.core.field.StandardUnaryOperator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.speedment.util.Util.instanceNotAllowed;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Utility class for converting CRUD operations into SQL strings.
 *
 * @author Emil
 */
public final class SqlWriter {

    /**
     * Prepares an SQL statement for the specified CRUD operation.
     *
     * @param con        the connection
     * @param operation  the CRUD operation
     * @return           the prepared statement
     */
    public static PreparedStatement prepare(Connection con, Operation operation) {
        try {
            return con.prepareStatement(toSql(operation));
        } catch (SQLException ex) {
            throw new SpeedmentException("Failed to parse SQL string into a PreparedStatement.", ex);
        }
    }

    /**
     * Converts the specified CRUD operation to an SQL string. This is a shortcut for the four methods
     * <ul>
     *     <li>{@link #create(Create)}
     *     <li>{@link #read(Read)}
     *     <li>{@link #update(Update)}
     *     <li>{@link #delete(Delete)}
     * </ul>
     *
     * @param operation  the operation to convert into SQL
     * @return           the SQL string
     */
    public static String toSql(Operation operation) {
        switch (operation.getType()) {
            case CREATE : return create((Create) operation);
            case READ   : return read((Read) operation);
            case UPDATE : return update((Update) operation);
            case DELETE : return delete((Delete) operation);
            default : throw new UnsupportedOperationException(
                "Unknown CRUD operation type '" + operation.getType().name() + "'."
            );
        }
    }

    /**
     * Creates an SQL query that represents the specified CRUD command.
     * <p>
     * Values will not be written in plain text but replaced with '?' characters.
     * To get a list of values, call the {@link #values(Valued)} method.
     *
     * @param create  the command to render
     * @return        the SQL query
     */
    public static String create(Create create) {
        final StringBuilder str = buildOperation(create);

        final Set<Map.Entry<Column, Object>> entries = create.getValues().entrySet();

        str.append(
            entries.stream()
                .map(Map.Entry::getKey)
                .map(Column::getName)
                .collect(joining(", ", " (", ") VALUES "))
        );

        str.append(
            entries.stream()
                .map(e -> "?")
                .collect(joining(", ", "(", ")"))
        );

        return str.append(";").toString();
    }

    /**
     * Creates an SQL query that represents the specified CRUD command.
     *
     * @param read  the command to render
     * @return      the SQL query
     */
    public static String read(Read read) {
        return buildOperation(read)
            .append(buildSelection(read))
            .append(buildLimit(read))
            .append(";")
            .toString();
    }

    /**
     * Creates an SQL query that represents the specified CRUD command.
     * <p>
     * Values will not be written in plain text but replaced with '?' characters.
     * To get a list of values, call the {@link #values(Valued)} method.
     *
     * @param update  the command to render
     * @return        the SQL query
     */
    public static String update(Update update) {
        return buildOperation(update)
            .append(
                update.getValues().entrySet().stream()
                    .map(e ->
                            formatColumnName(e.getKey()) +
                                " = " +
                                formatString(escapeValue(e.getValue()))
                    )
                    .collect(joining(", "))
            )
            .append(buildSelection(update))
            .append(buildLimit(update))
            .append(";").toString();
    }

    /**
     * Creates an SQL query that represents the specified CRUD command.
     *
     * @param delete  the command to render
     * @return        the SQL query
     */
    public static String delete(Delete delete) {
        return buildOperation(delete)
            .append(buildSelection(delete))
            .append(buildLimit(delete))
            .append(";")
            .toString();
    }

    /**
     * Returns a list of the values specified in the operation with the order preserved.
     *
     * @param operation  the operation
     * @return           the list of values
     */
    public static List<Object> values(Valued operation) {
        return operation.getValues().values().stream().collect(toList());
    }

    /**
     * Returns a {@code StringBuilder} with the first part of the sql query for the specified operation.
     *
     * @param operation  the operation
     * @return           a builder with the first part of the query
     */
    private static StringBuilder buildOperation(Operation operation) {
        final StringBuilder str = new StringBuilder();

        switch (operation.getType()) {
            case CREATE : str.append("INSERT INTO "); break;
            case READ   : str.append("SELECT * FROM "); break;
            case UPDATE : str.append("UPDATE "); break;
            case DELETE : str.append("DELETE FROM "); break;
            default : throw new UnsupportedOperationException(
                "Unknown CRUD operation type '" + operation.getType().name() + "'."
            );
        }

        str.append(formatTableName(operation.getTable()));

        if (operation.getType() == Operation.Type.UPDATE) {
            str.append(" SET ");
        }

        return str;

    }

    /**
     * Builds the selection part of the sql query.
     *
     * @param selective  the operation
     * @return           the selection part
     */
    private static String buildSelection(Selective selective) {
        return selective.getSelectors()
            .map(sel ->
                    formatColumnName(sel.getColumn()) +
                        formatOperator(sel.getOperator(), sel.getOperand())
            )
            .collect(joining(" AND ", " WHERE ", ""));
    }

    /**
     * Builds the limit part of the sql query.
     *
     * @param selective  the operation
     * @return           the limit part
     */
    private static Optional<String> buildLimit(Selective selective) {
        final long limit = selective.getLimit();

        if (limit > 0 && limit != Long.MAX_VALUE) {
            return Optional.of(" LIMIT " + limit);
        } else return Optional.empty();
    }

    /**
     * Returns the name of the specified table formatted as appropriate for use in an SQL query.
     *
     * @param table  the table
     * @return       the formatted table name
     */
    private static String formatTableName(Table table) {
        return table.getTableName().orElse(table.getName());
    }

    /**
     * Returns the name of the specified column formatted as appropriate for use in an SQL query.
     *
     * @param column  the column
     * @return        the formatted column name
     */
    private static String formatColumnName(Column column) {
        return "`" + column.getName() + "`";
    }

    /**
     * Returns a string representation of the specified operator and operand formatted as appropriate in SQL.
     *
     * @param operator  the operator
     * @param operand   the operand
     * @return          the formatted text
     */
    private static String formatOperator(Operator operator, Optional<Object> operand) {

        if (operator instanceof StandardUnaryOperator) {
            @SuppressWarnings("unchecked")
            final StandardUnaryOperator op = (StandardUnaryOperator) operator;

            switch (op) {
                case IS_NULL     : return " = NULL";
                case IS_NOT_NULL : return " <> NULL";
                default : throw new UnsupportedOperationException("Unknown unary operator '" + op.name() + "'.");
            }
        } else if (operator instanceof StandardBinaryOperator) {
            @SuppressWarnings("unchecked")
            final StandardBinaryOperator op = (StandardBinaryOperator) operator;
            final String value = formatString(escapeValue(operand.get()));

            switch (op) {
                case EQUAL            : return " = " + value;
                case NOT_EQUAL        : return " <> " + value;
                case LESS_THAN        : return " < " + value;
                case LESS_OR_EQUAL    : return " <= " + value;
                case GREATER_THAN     : return " > " + value;
                case GREATER_OR_EQUAL : return " >= " + value;
                default : throw new UnsupportedOperationException("Unknown binary operator '" + op.name() + "'.");
            }
        } else if (operator instanceof StandardStringBinaryOperator) {
            @SuppressWarnings("unchecked")
            final StandardStringBinaryOperator op = (StandardStringBinaryOperator) operator;

            switch (op) {
                case STARTS_WITH            : return " LIKE " + formatString(escapeValue(operand.get()) + "%");
                case ENDS_WITH              : return " LIKE " + formatString("%" + escapeValue(operand.get()));
                case CONTAINS               : return " LIKE " + formatString("%" + escapeValue(operand.get()) + "%");
                case EQUAL_IGNORE_CASE      : return " = " + formatString(escapeValue(operand.get()));
                case NOT_EQUAL_IGNORE_CASE  : return " <> " + formatString(escapeValue(operand.get()));
                default : throw new UnsupportedOperationException("Unknown string operator '" + op.name() + "'.");
            }
        }

        return operator.toString();
    }

    /**
     * Formats the specified value as a SQL string.
     *
     * @param value  the string
     * @return       the formatted string
     */
    private static String formatString(String value) {
        return "\"" + value.toString() + "\"";
    }

    /**
     * Escapes some characters in the specified value so that it can be used in an SQL query.
     *
     * @param value  the value to escape
     * @return       the escaped value
     */
    private static String escapeValue(Object value) {
        if (value == null) {
            return "NULL";
        } else {
            return value.toString()
                .replace("%", "\\%")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("`", "\\`");
        }
    }

    /**
     * Utility classes should not be instantiated.
     */
    private SqlWriter() { instanceNotAllowed(getClass()); }
}
