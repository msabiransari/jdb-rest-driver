package org.jrest.driver.test;


import org.jdbrest.driver.JdbRestDriver;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class JdbRestDriverTest {
  @Test
  public void whenQueryIsExecuted_itReturnsResults() {
    try {
      DriverManager.registerDriver(new JdbRestDriver());
      Connection connection = DriverManager.getConnection("jdbc:http:localhost:8087:/employees:", "user", "password");
      Statement stmt = connection.createStatement();
      ResultSet resultSet = stmt.executeQuery("select * from employees where dept = 10");
      /*while(resultSet.next()) {

      }*/
      assertNotNull(connection);
    } catch(Exception e) {
      fail();
    }
  }
}
