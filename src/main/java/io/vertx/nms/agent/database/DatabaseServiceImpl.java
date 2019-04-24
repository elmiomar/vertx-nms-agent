
package io.vertx.nms.agent.database;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.SingleHelper;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

class DatabaseServiceImpl implements DatabaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServiceImpl.class);

  private final HashMap<SqlQuery, String> sqlQueries;
  private final JDBCClient dbClient;

  DatabaseServiceImpl(io.vertx.ext.jdbc.JDBCClient dbClient, HashMap<SqlQuery, String> sqlQueries,
      Handler<AsyncResult<DatabaseService>> readyHandler) {
    this.dbClient = new JDBCClient(dbClient);
    this.sqlQueries = sqlQueries;

    SQLClientHelper
        .usingConnectionSingle(this.dbClient,
            conn -> conn.rxExecute(sqlQueries.get(SqlQuery.CREATE_FACES_TABLE))
                .andThen(conn.rxExecute(sqlQueries.get(SqlQuery.CREATE_FIB_TABLE)))
                .andThen(conn.rxExecute(sqlQueries.get(SqlQuery.CREATE_LOGS_TABLE))).andThen(Single.just(this)))
        .subscribe(SingleHelper.toObserver(readyHandler));
  }

  // Faces queries

  @Override
  public DatabaseService fetchAllFaces(Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    dbClient.rxQuery(sqlQueries.get(SqlQuery.ALL_FACES)).map(ResultSet::getRows)
        .subscribe(SingleHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService fetchFaceById(int id, Handler<AsyncResult<JsonObject>> resultHandler) {
    Single<ResultSet> resultSet = dbClient.rxQueryWithParams(sqlQueries.get(SqlQuery.GET_FACE_BY_ID),
        new JsonArray().add(id));
    resultSet.map(result -> {
      if (result.getNumRows() > 0) {
        JsonObject row = result.getRows().get(0);
        return new JsonObject().put("found", true).put("id", row.getInteger("ID"))
            .put("remote", row.getString("REMOTE")).put("local", row.getString("LOCAL"));
      } else {
        return new JsonObject().put("found", false);
      }
    }).subscribe(SingleHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService createFace(int id, String remote, String local, Handler<AsyncResult<Void>> resultHandler) {
    dbClient.rxUpdateWithParams(sqlQueries.get(SqlQuery.CREATE_FACE), new JsonArray().add(id).add(remote).add(local))
        .toCompletable().subscribe(CompletableHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService saveFace(int id, String remote, String local, Handler<AsyncResult<Void>> resultHandler) {
    dbClient.rxUpdateWithParams(sqlQueries.get(SqlQuery.SAVE_FACE), new JsonArray().add(remote).add(local).add(id))
        .toCompletable().subscribe(CompletableHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService deleteFace(int id, Handler<AsyncResult<Void>> resultHandler) {
    JsonArray data = new JsonArray().add(id);
    dbClient.rxUpdateWithParams(sqlQueries.get(SqlQuery.DELETE_FACE), data).toCompletable()
        .subscribe(CompletableHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService deleteAllFaces(Handler<AsyncResult<Void>> resultHandler) {
    dbClient.rxQuery(sqlQueries.get(SqlQuery.DELETE_ALL_FACES)).toCompletable()
        .subscribe(CompletableHelper.toObserver(resultHandler));
    return this;
  }

  // FIB queries

  @Override
  public DatabaseService createFibEntry(String prefix, int faceId, int cost, Handler<AsyncResult<Void>> resultHandler) {
    dbClient
        .rxUpdateWithParams(sqlQueries.get(SqlQuery.CREATE_FIB_ENTRY),
            new JsonArray().add(prefix).add(faceId).add(cost))
        .toCompletable().subscribe(CompletableHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService fetchAllFibEntries(Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    dbClient.rxQuery(sqlQueries.get(SqlQuery.ALL_FIB_ENTRIES)).map(ResultSet::getRows)
        .subscribe(SingleHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService fetchFibEntry(String name, Handler<AsyncResult<JsonObject>> resultHandler) {
    // not implemented yet
    return this;
  }

  @Override
  public DatabaseService fetchFibEntryById(int id, Handler<AsyncResult<JsonObject>> resultHandler) {
    Single<ResultSet> resultSet = dbClient.rxQueryWithParams(sqlQueries.get(SqlQuery.GET_FIB_ENTRY_BY_ID),
        new JsonArray().add(id));
    resultSet.map(result -> {
      if (result.getNumRows() > 0) {
        JsonObject row = result.getRows().get(0);
        return new JsonObject().put("found", true).put("id", row.getInteger("ID"))
            .put("prefix", row.getString("PREFIX")).put("face", row.getInteger("FACE"))
            .put("cost", row.getInteger("COST"));
      } else {
        return new JsonObject().put("found", false);
      }
    }).subscribe(SingleHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService saveFibEntry(String prefix, int faceId, int cost, String id,
      Handler<AsyncResult<Void>> resultHandler) {
    dbClient
        .rxUpdateWithParams(sqlQueries.get(SqlQuery.SAVE_FIB_ENTRY),
            new JsonArray().add(prefix).add(faceId).add(cost).add(id))
        .toCompletable().subscribe(CompletableHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService deleteFibEntry(int entryId, Handler<AsyncResult<Void>> resultHandler) {
    JsonArray data = new JsonArray().add(entryId);
    dbClient.rxUpdateWithParams(sqlQueries.get(SqlQuery.DELETE_FIB_ENTRY), data).toCompletable()
        .subscribe(CompletableHelper.toObserver(resultHandler));
    return this;
  }

  // LOGS calls
  @Override
  public DatabaseService fetchAllLogs(Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    dbClient.rxQuery(sqlQueries.get(SqlQuery.ALL_LOGS)).map(ResultSet::getRows)
        .subscribe(SingleHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService fetchLogById(int logId, Handler<AsyncResult<JsonObject>> resultHandler) {
    Single<ResultSet> resultSet = dbClient.rxQueryWithParams(sqlQueries.get(SqlQuery.GET_LOG_BY_ID),
        new JsonArray().add(logId));
    resultSet.map(result -> {
      if (result.getNumRows() > 0) {
        JsonObject row = result.getRows().get(0);
        return new JsonObject().put("found", true).put("id", row.getInteger("ID"))
            .put("timestamp", row.getString("TIMESTAMP")).put("verticle", row.getString("VERTICLE"))
            .put("level", row.getString("LEVEL")).put("message", row.getString("MESSAGE"));
      } else {
        return new JsonObject().put("found", false);
      }
    }).subscribe(SingleHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService createLog(String timestamp, String verticle, String level, String message,
      Handler<AsyncResult<Void>> resultHandler) {
    dbClient
        .rxUpdateWithParams(sqlQueries.get(SqlQuery.CREATE_LOG),
            new JsonArray().add(timestamp).add(verticle).add(level).add(message))
        .toCompletable().subscribe(CompletableHelper.toObserver(resultHandler));
    return this;
  }

  @Override
  public DatabaseService deleteLog(int logId, Handler<AsyncResult<Void>> resultHandler) {
    JsonArray data = new JsonArray().add(logId);
    dbClient.rxUpdateWithParams(sqlQueries.get(SqlQuery.DELETE_LOG), data).toCompletable()
        .subscribe(CompletableHelper.toObserver(resultHandler));
    return this;
  }

}
