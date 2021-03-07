package jdbc.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class JDBCURLBuilder {

    protected static final String JDBC = "jdbc";
    protected Map<String, String> properties;
    protected String dbType;
    protected int portNumber;
    protected String hostAddress;
    protected String catalogName;

    public JDBCURLBuilder() {
        properties = new LinkedHashMap<>();
    }

    //	void setPort( String port);
    public void setPort(String port) {
        Objects.requireNonNull(port);
        try {
            int portNum = Integer.parseInt(port);
            if (portNum < 0) {
                throw new IllegalArgumentException("negative port error");
            }
            portNumber = portNum;
        } catch (NumberFormatException e) {
        }
    }

    //	void addURLProperty( String key, String value);
    public void addURLProperty(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        properties.put(key, value);
    }

    //  setDB
    protected void setDB(String db) {
        Objects.requireNonNull(db);
        dbType = db;
    }

    //	String getURL();
    public abstract String getURL();


    //	void setPort( int port);
    public void setPortNumber(int portNumber) {
        if (portNumber > 0) {
            this.portNumber = portNumber;
        }
    }

    //	void setAddress( String address);
    public void setAddress(String address) {
        Objects.requireNonNull(address);
        this.hostAddress = address;
    }

    //	void setCatalog( String catalog);
    public void setCatalog(String catalog) {
        Objects.requireNonNull(catalog);
        catalogName = catalog;
    }

}