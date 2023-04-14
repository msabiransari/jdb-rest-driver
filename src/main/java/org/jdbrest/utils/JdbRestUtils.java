package org.jdbrest.utils;

import com.sun.jdi.BooleanValue;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.jdbrest.driver.model.QueryStatement;

import java.util.List;
import java.util.Map;

public class JdbRestUtils {
  private static final String HEADER_PREFIX = "header_";
  public static QueryStatement parseQuery(String query) {
    QueryStatement queryStatement = new QueryStatement();
    Statement statement;

    try {
      statement = CCJSqlParserUtil.parse(query, ccjSqlParser -> ccjSqlParser.withSquareBracketQuotation(true));
    } catch(Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error while parsing statement", e);
    }

    if (statement instanceof Select select) {
      PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

      Table table = (Table) plainSelect.getFromItem();

      queryStatement.processTableName(table.getFullyQualifiedName());

      List<SelectItem> selectItems = plainSelect.getSelectItems();
      processSelectItems(queryStatement, selectItems);

      processParameters(plainSelect.getWhere(), queryStatement.getParameters(), queryStatement.getHeaders());

    } else {
      throw new RuntimeException("This driver does not support any statement other then Select");
    }
    return queryStatement;
  }

  private static void processSelectItems(QueryStatement queryStatement, List<SelectItem> selectItems) {
    List<String> columns = selectItems.stream().map(Object::toString).toList();
    queryStatement.getParameters().put("data_columns", columns);
  }

  private static void processParameters(Expression expression, Map<String, Object> parameters, Map<String, String> headers) {
    if(expression instanceof AndExpression) {
      Expression leftExpression = ((AndExpression) expression).getLeftExpression();
      processParameters(leftExpression, parameters, headers);
      processExpression(((AndExpression) expression).getRightExpression(), parameters, headers);
    } else {
      processExpression(expression, parameters, headers);
    }
  }

  private static void processExpression(Expression expression, Map<String, Object> parameters, Map<String, String> headers) {
    if(expression instanceof EqualsTo) {
      Column column = (Column) ((EqualsTo) expression).getLeftExpression();
      Expression rightExpression = ((EqualsTo) expression).getRightExpression();
      Object value;

      if(rightExpression instanceof LongValue) {
        value = ((LongValue) rightExpression).getValue();
      } else if (rightExpression instanceof StringValue) {
        value = ((StringValue) rightExpression).getValue();
      } else if (rightExpression instanceof DateValue) {
        value = ((DateValue) rightExpression).getValue();
      } else if (rightExpression instanceof DoubleValue) {
        value = ((DoubleValue) rightExpression).getValue();
      } else if (rightExpression instanceof BooleanValue) {
        value = ((BooleanValue) rightExpression).value();
      } else if (rightExpression instanceof Column) {
        value = ((Column) rightExpression).getColumnName();
        if(value.toString().equalsIgnoreCase("true") || value.toString().equalsIgnoreCase("false")) {
          value = Boolean.valueOf(value.toString());
        }
      } else {
        throw new RuntimeException("Invalid Type Found.");
      }
      String columnName = column.getColumnName();
      if(columnName.startsWith(HEADER_PREFIX)) {
        headers.put(columnName.substring(HEADER_PREFIX.length()), value.toString());
      } else {
        parameters.put(columnName, value);
      }
    } else {
      throw new RuntimeException("Driver does not support any operator other then EQUALS");
    }
  }
}
