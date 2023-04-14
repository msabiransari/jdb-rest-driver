package org.jdbrest.driver;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class JdbRestDriver implements Driver {

  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    //jdbc:http:server:port:url:queryString
    String[] urlComponents = url.split(":");

    String httpUrl = String.format("%s://%s%s%s%s",
        urlComponents[1],
        urlComponents[2],
        (urlComponents[3].length() == 0) ? "" : ":" + urlComponents[3],
        urlComponents[4],
        urlComponents.length == 6 ? urlComponents[5] : ""
    );
    return new JdbRestConnection(httpUrl, "", "");
  }

  @Override
  public boolean acceptsURL(String url) throws SQLException {
    String[] urlComponents = url.split(":");
    if(urlComponents.length == 6) {
      return urlComponents[0].equals("jdbc") &&
          (urlComponents[1].equals("http") || urlComponents[1].equals("https")) &&
          urlComponents[2].length() > 0 &&
          urlComponents[3].length() > 0 && Integer.parseInt(urlComponents[3]) > 0 &&
          urlComponents[4].length() > 0;
    }
    return false;
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
    return new DriverPropertyInfo[0];
  }

  @Override
  public int getMajorVersion() {
    return 0;
  }

  @Override
  public int getMinorVersion() {
    return 0;
  }

  @Override
  public boolean jdbcCompliant() {
    return false;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return null;
  }
}
