package jdbc;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jdbc.builder.JDBCURLBuilder;

import java.sql.SQLException;
import java.util.List;

public class JDBCController implements AutoCloseable {
    private JDBCURLBuilder builder;
    private JDBCModel model;
    private StringProperty tableInUse;
    private ObservableList<String> tableNamesList;

    public JDBCController() {
        tableNamesList = FXCollections.observableArrayList();
        model = new JDBCModel();
        tableInUse = new SimpleStringProperty();
        tableInUse.addListener((value, oldValue, newValue) -> {
            try {
                model.getAndInitializeColumnNames(newValue);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void close() throws Exception {
        model.close();
    }

    //	StringProperty tableInUseProperty();
    public StringProperty tableInUseProperty() {
        return tableInUse;
    }

    //	JDBCController setURLBuilder( JDBCURLBuilder builder);
    public JDBCController setURLBuilder(JDBCURLBuilder builder) {
        this.builder = builder;
        return this;
    }

    //	JDBCController setDataBase( String address, String port, String catalog);
    public JDBCController setDataBase(String address, String port, String catalog) {
        builder.setAddress(address);
        builder.setPort(port);
        builder.setCatalog(catalog);
        return this;
    }

    //	JDBCController addConnectionURLProperty( String key, String value);
    public JDBCController addConnectionURLProperty(String key, String value) {
        builder.addURLProperty(key, value);
        return this;
    }

    //	JDBCController setCredentials( String user, String pass);
    public JDBCController setCredentials(String user, String pass) {
        model.setCredentials(user, pass);
        return this;

    }

    //	JDBCController connect() throws SQLException;
    public JDBCController connect() throws SQLException {
        model.connectTo(builder.getURL());
        return this;
    }

    //	boolean isConnected() throws SQLException;
    public boolean isConnected() throws SQLException {
        return model.isConnected();

    }

    //	List< String> getColumnNames() throws SQLException;
    public List<String> getColumnNames() throws SQLException {
        return model.getAndInitializeColumnNames(tableInUse.get());
    }

    //	ObservableList< String> getTableNames() throws SQLException;
    public ObservableList<String> getTableNames() throws SQLException {
        if (model.isConnected()) {
            tableNamesList.clear();
            tableNamesList.addAll(model.getTableNames());
        }
        return tableNamesList;
    }

    //	List< List< Object>> getAll() throws SQLException;
    public List<List<Object>> getAll() throws SQLException {
        List<List<Object>> modelAll = model.getAll(tableInUse.get());
        return modelAll;
    }

    //	List< List< Object>> search( String searchTerm) throws SQLException;
    public List<List<Object>> search(String searchTerm) throws SQLException {
        return model.search(tableInUse.get(), searchTerm);
    }


}
