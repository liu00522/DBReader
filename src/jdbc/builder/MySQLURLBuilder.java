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
        StringBuilder sb = new StringBuilder();

        sb.append(MySQLURLBuilder.JDBC + ":");
        sb.append(dbType + "://");
        sb.append(hostAddress + ":");
        sb.append(portNumber + "/");
        sb.append(catalogName + "?");
        Iterator<Map.Entry<String, String>> it = properties.entrySet().iterator();
        do {
            Map.Entry<String, String> entry = (Entry<String, String>) it.next();
            sb.append(entry.getKey() + "=" + entry.getValue());
            if (it.hasNext()) {
                sb.append("&");
            }
        } while ((it.hasNext()));
        return sb.toString();
    }
}
