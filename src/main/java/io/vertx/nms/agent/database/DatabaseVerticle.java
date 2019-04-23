
package io.vertx.nms.agent.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceBinder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class DatabaseVerticle extends AbstractVerticle {

  public static final String CONFIG_DB_JDBC_URL = "db.jdbc.url";
  public static final String CONFIG_DB_JDBC_DRIVER_CLASS = "db.jdbc.driver_class";
  public static final String CONFIG_DB_JDBC_MAX_POOL_SIZE = "db.jdbc.max_pool_size";
  public static final String CONFIG_DB_SQL_QUERIES_RESOURCE_FILE = "db.sqlqueries.resource.file";
  public static final String CONFIG_DB_QUEUE = "db.queue";

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    HashMap<SqlQuery, String> sqlQueries = loadSqlQueries();

    JDBCClient dbClient = JDBCClient.createShared(vertx, new JsonObject()
      .put("url", config().getString(CONFIG_DB_JDBC_URL, "jdbc:hsqldb:file:db/"))
      .put("driver_class", config().getString(CONFIG_DB_JDBC_DRIVER_CLASS, "org.hsqldb.jdbcDriver"))
      .put("max_pool_size", config().getInteger(CONFIG_DB_JDBC_MAX_POOL_SIZE, 30)));

    DatabaseService.create(dbClient, sqlQueries, ready -> {
      if (ready.succeeded()) {
        ServiceBinder binder = new ServiceBinder(vertx);
        binder.setAddress(CONFIG_DB_QUEUE).register(DatabaseService.class, ready.result());
        startFuture.complete();
      } else {
        startFuture.fail(ready.cause());
      }
    });
  }

  /*
   * Note: this uses blocking APIs, but data is small...
   */
  private HashMap<SqlQuery, String> loadSqlQueries() throws IOException {

    String queriesFile = config().getString(CONFIG_DB_SQL_QUERIES_RESOURCE_FILE);
    InputStream queriesInputStream;
    if (queriesFile != null) {
      queriesInputStream = new FileInputStream(queriesFile);
    } else {
      queriesInputStream = getClass().getResourceAsStream("/db-queries.properties");
    }

    Properties queriesProps = new Properties();
    queriesProps.load(queriesInputStream);
    queriesInputStream.close();

    HashMap<SqlQuery, String> sqlQueries = new HashMap<>();
    sqlQueries.put(SqlQuery.CREATE_FACES_TABLE, queriesProps.getProperty("create-faces-table"));
    sqlQueries.put(SqlQuery.ALL_FACES, queriesProps.getProperty("all-faces"));
    sqlQueries.put(SqlQuery.GET_FACE, queriesProps.getProperty("get-face"));
    sqlQueries.put(SqlQuery.CREATE_FACE, queriesProps.getProperty("create-face"));
    sqlQueries.put(SqlQuery.SAVE_FACE, queriesProps.getProperty("save-face"));
    sqlQueries.put(SqlQuery.DELETE_FACE, queriesProps.getProperty("delete-face"));
    sqlQueries.put(SqlQuery.DELETE_ALL_FACES, queriesProps.getProperty("delete-all-faces"));
    sqlQueries.put(SqlQuery.GET_FACE_BY_ID, queriesProps.getProperty("get-face-by-id"));

    sqlQueries.put(SqlQuery.CREATE_FIB_TABLE, queriesProps.getProperty("create-fib-table"));
    sqlQueries.put(SqlQuery.ALL_FIB, queriesProps.getProperty("all-fib"));
    sqlQueries.put(SqlQuery.GET_FIB_ENTRY, queriesProps.getProperty("get-fib-entry"));
    sqlQueries.put(SqlQuery.CREATE_FIB_ENTRY, queriesProps.getProperty("create-fib-entry"));
    sqlQueries.put(SqlQuery.SAVE_FIB_ENTRY, queriesProps.getProperty("save-fib-entry"));
    sqlQueries.put(SqlQuery.DELETE_FIB_ENTRY, queriesProps.getProperty("delete-fib-entry"));
    sqlQueries.put(SqlQuery.GET_FIB_BY_PREFIX, queriesProps.getProperty("get-fib-by-prefix"));
    return sqlQueries;
  }
}
