package jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class JDBCModel {

    private List<String> columnNames;
    private List<String> tableNames;
    private Connection connection;
    private String user;
    private String pass;



    JDBCModel() {
        columnNames = new LinkedList<>();
        tableNames = new LinkedList<>();
    }

    public void setCredentials(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    private void checkConnectionIsValid() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Invalid Connection");
        }
    }

    private void checkTableNameAndColumnsAreValid(String table) throws SQLException {
        Objects.requireNonNull(table, "table name cannot be null");
        table = table.trim();
        if (tableNames.isEmpty()) {
            getAndInitializeTableNames();
        }

        if (columnNames.isEmpty()) {
            getAndInitializeColumnNames(table);
        }

        if (table.isEmpty() || !tableNames.contains(table)) {
            throw new IllegalArgumentException("table name=\"" + table + "\" is not valid");
        }

    }

    public void connectTo(String url) throws SQLException {
        if (connection == null)
            connection = DriverManager.getConnection(url, user, pass);

    }

    public boolean isConnected() throws SQLException {
        if (connection != null) {
            return !connection.isClosed();
        }
        return false;
    }

    public List<String> getAndInitializeColumnNames(String table) throws SQLException {
        checkConnectionIsValid();

        columnNames.clear();

        DatabaseMetaData dbMeta = connection.getMetaData();
        try (ResultSet rs = dbMeta.getColumns(connection.getCatalog(), null, table, null)) {
            while (rs.next()) {
                columnNames.add(rs.getString("COLUMN_NAME"));
            }
        }
        List<String> list = Collections.unmodifiableList(columnNames);

        return list;

    }

    private List<String> getAndInitializeTableNames() throws SQLException {
        checkConnectionIsValid();

        tableNames.clear();

        DatabaseMetaData dbMeta = connection.getMetaData();
        try (ResultSet rs = dbMeta.getTables(connection.getCatalog(), null, null, new String[]{"TABLE"})) {
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
        }
        List<String> list = Collections.unmodifiableList(tableNames);

        return list;

    }

    public List<List<Object>> getAll(String table) throws SQLException {
        return search(table, null);
    }

    public List<List<Object>> search(String table, String searchTerm) throws SQLException {
        checkConnectionIsValid();
        checkTableNameAndColumnsAreValid(table);
        List<List<Object>> list = new LinkedList<List<Object>>();
        String query = buildSQLSearchQuery(table, (searchTerm.isEmpty() || searchTerm == null) ? false : true);
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if (searchTerm != null && !searchTerm.isEmpty()) {
                searchTerm = String.format("%%%s%%", searchTerm);
                for (int i = 1; i <= columnNames.size(); i++) {
                    ps.setObject(i, searchTerm);
                }
            }
            extractRowsFromResultSet(ps, list);
        }
        return list;
    }

    private String buildSQLSearchQuery(String table, boolean withParameters) {
        StringBuilder sb = new StringBuilder();

        sb.append("select * from " + table);
        if (withParameters) {
            sb.append(" where ");
            Iterator<String> it = columnNames.iterator();
            do {
                String entry = it.next();
                sb.append(entry + " like ?");
                if (it.hasNext()) {
                    sb.append(" or ");
                }

            } while (it.hasNext());
        }
        return sb.toString();
    }

    private void extractRowsFromResultSet(PreparedStatement ps, List<List<Object>> list) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnNames.size(); i++) {
                    row.add(rs.getObject(i));
                }
                list.add(row);
            }
        }
    }

    public List<String> getTableNames() {
        if (tableNames.isEmpty()) {
            try {
                getAndInitializeTableNames();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return tableNames;
    }

    public void close() throws Exception {
        if (connection != null)
            connection.close();
    }

}
