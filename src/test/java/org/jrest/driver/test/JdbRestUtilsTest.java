package org.jrest.driver.test;

import org.jdbrest.driver.model.QueryStatement;
import org.jdbrest.utils.JdbRestUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JdbRestUtilsTest {

  @Test
  public void whenQueryIsProvidedWithDefaultGet_itParsesItSuccessfully() {
    QueryStatement statement = JdbRestUtils.parseQuery("select name, salary " +
        "from [jdb-rest].employees.data " +
        "where dept_id = 20 and name = 'Kevin T' and active = true and header_sessionId = 'secretKey'");

    validate(statement, "/jdb-rest/employees/data", "get");
  }

  @Test
  public void whenQueryIsProvidedWithSpecifiedGet_itParsesItSuccessfully() {
    QueryStatement statement = JdbRestUtils.parseQuery("select name, salary " +
        "from [jdb-rest].employees.data.get " +
        "where dept_id = 20 and name = 'Kevin T' and active = true and header_sessionId = 'secretKey'");

    validate(statement, "/jdb-rest/employees/data", "get");
  }

  @Test
  public void whenQueryIsProvidedWithPost_itParsesItSuccessfully() {
    QueryStatement statement = JdbRestUtils.parseQuery("select name, salary " +
        "from [jdb-rest].employees.data.post " +
        "where dept_id = 20 and name = 'Kevin T' and active = true and header_sessionId = 'secretKey'");

    validate(statement, "/jdb-rest/employees/data", "post");
  }

  @Test
  public void whenQueryIsProvidedWithShortTableNameAndGetMethod_itParsesItSuccessfully() {
    QueryStatement statement = JdbRestUtils.parseQuery("select name, salary " +
        "from [jdb-rest].employees " +
        "where dept_id = 20 and name = 'Kevin T' and active = true and header_sessionId = 'secretKey'");

    validate(statement, "/jdb-rest/employees", "get");
  }

  @Test
  public void whenQueryIsProvidedWithShortTableNameAndPostMethod_itParsesItSuccessfully() {
    QueryStatement statement = JdbRestUtils.parseQuery("select name, salary " +
        "from [jdb-rest].employees.post " +
        "where dept_id = 20 and name = 'Kevin T' and active = true and header_sessionId = 'secretKey'");

    validate(statement, "/jdb-rest/employees", "post");
  }

  @Test
  public void whenQueryIsProvidedWithVeryShortTableName_itParsesItSuccessfully() {
    QueryStatement statement = JdbRestUtils.parseQuery("select name, salary " +
        "from employees " +
        "where dept_id = 20 and name = 'Kevin T' and active = true and header_sessionId = 'secretKey'");

    validate(statement, "/employees", "get");
  }

  @Test
  public void whenQueryIsProvidedWithVeryShortTableNameAndPost_itParsesItSuccessfully() {
    QueryStatement statement = JdbRestUtils.parseQuery("select name, salary " +
        "from employees.post " +
        "where dept_id = 20 and name = 'Kevin T' and active = true and header_sessionId = 'secretKey'");

    validate(statement, "/employees", "post");
  }

  @Test
  public void whenQueryIsProvidedWithSingleTableNameAndPost_itParsesItSuccessfully() {
    QueryStatement statement = JdbRestUtils.parseQuery("select name, salary " +
        "from [employees-all] " +
        "where dept_id = 20 and name = 'Kevin T' and active = true and header_sessionId = 'secretKey'");

    validate(statement, "/employees-all", "get");
  }

  @Test
  public void whenQueryIsProvidedWithSingleTableNameAndGet_itParsesItSuccessfully() {
    QueryStatement statement = JdbRestUtils.parseQuery("select name, salary " +
        "from [employees-all].get " +
        "where dept_id = 20 and name = 'Kevin T' and active = true and header_sessionId = 'secretKey'");

    validate(statement, "/employees-all", "get");
  }

  private void validate(QueryStatement statement, String expectedUrl, String expectedMethod) {
    assertNotNull(statement);

    //Method and URL
    assertEquals(expectedUrl, statement.getUrl());
    assertEquals(expectedMethod, statement.getMethod());

    //Parameters
    Map<String, Object> parameters = statement.getParameters();
    assertNotNull(parameters);
    assertEquals(4, parameters.size());
    assertEquals(20L, parameters.get("dept_id"));
    assertEquals("Kevin T", parameters.get("name"));
    assertEquals(Boolean.TRUE, parameters.get("active"));

    //Data Columns
    List<String> columnNames = (List<String>) parameters.get("data_columns");
    assertEquals(2, columnNames.size());
    assertTrue(columnNames.contains("name"));
    assertTrue(columnNames.contains("salary"));

    //Headers
    assertNotNull(statement.getHeaders());
    assertEquals(1, statement.getHeaders().size());
    assertEquals("secretKey", statement.getHeaders().get("sessionId"));
  }
}