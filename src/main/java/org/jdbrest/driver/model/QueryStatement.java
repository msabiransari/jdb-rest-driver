package org.jdbrest.driver.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;


@NoArgsConstructor
public class QueryStatement {
  private String method;
  private String url;
  private Map<String, String> headers = new HashMap<>();
  private Map<String, Object> parameters = new HashMap<>();

  private static final Set<String> supportedMethods = new HashSet<>(Arrays.asList("get", "post"));

  public String getMethod() {
    return method;
  }

  public String getUrl() {
    return url;
  }

  public void processTableName(String tableName) {
    String[] tempUrl = tableName.replaceAll("\\[", "")
        .replaceAll("]", "").toLowerCase().split("\\.");

    String method = tempUrl[tempUrl.length - 1];

    StringBuilder builder = new StringBuilder();

    if(supportedMethods.contains(method)) {
      this.method = method;
      for(int index = 0; index < tempUrl.length -1; index ++) {
        builder.append("/").append(tempUrl[index]);
      }
    } else {
      this.method = "get";
      for (String s : tempUrl) {
        builder.append("/").append(s);
      }
    }
    this.url = builder.toString();
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public Map<String, Object> getParameters() {
    return parameters;
  }
}
