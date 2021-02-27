package jdbc.builder;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MySQLURLBuilder extends JDBCURLBuilder {

    public MySQLURLBuilder() {
        setDB("mysql");
    }



    @Override
    public String getURL() {
        StringBuilder str = new StringBuilder();

        str.append(MySQLURLBuilder.JDBC + ":");
        str.append(dbType + "://");
        str.append(hostAddress + ":");
        str.append(portNumber + "/");
        str.append(catalogName + "?");
        Iterator<Map.Entry<String, String>> it = properties.entrySet().iterator();
        do {
            Map.Entry<String, String> entry = (Entry<String, String>) it.next();
            str.append(entry.getKey() + "=" + entry.getValue());
            if (it.hasNext()) {
                str.append("&");
            }
        } while ((it.hasNext()));
        return str.toString();
    }
}
