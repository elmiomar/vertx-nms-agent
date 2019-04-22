package io.vertx.nms.agent.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.HashMap;
import java.util.List;

@ProxyGen
@VertxGen
public interface DatabaseService {

  @GenIgnore
  static DatabaseService create(JDBCClient dbClient, HashMap<SqlQuery, String> sqlQueries, Handler<AsyncResult<DatabaseService>> readyHandler) {
    return new DatabaseServiceImpl(dbClient, sqlQueries, readyHandler);
  }

  @GenIgnore
  static io.vertx.nms.agent.database.reactivex.DatabaseService createProxy(Vertx vertx, String address) {
    return new io.vertx.nms.agent.database.reactivex.DatabaseService(new DatabaseServiceVertxEBProxy(vertx, address));
  }

  @Fluent
  DatabaseService fetchAllFaces(Handler<AsyncResult<JsonArray>> resultHandler);

  @Fluent
  DatabaseService fetchFace(String name, Handler<AsyncResult<JsonObject>> resultHandler);

  @Fluent
  DatabaseService fetchFaceById(int id, Handler<AsyncResult<JsonObject>> resultHandler);

  @Fluent
  DatabaseService createFace(int id, String remote, String local, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  DatabaseService saveFace(int id, String remote, String local, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  DatabaseService deleteFace(int id, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  DatabaseService fetchAllFacesData(Handler<AsyncResult<List<JsonObject>>> resultHandler);
}