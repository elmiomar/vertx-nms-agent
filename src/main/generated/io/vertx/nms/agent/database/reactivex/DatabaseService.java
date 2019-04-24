/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.nms.agent.database.reactivex;

import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import java.util.List;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


@io.vertx.lang.rx.RxGen(io.vertx.nms.agent.database.DatabaseService.class)
public class DatabaseService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DatabaseService that = (DatabaseService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.rx.TypeArg<DatabaseService> __TYPE_ARG = new io.vertx.lang.rx.TypeArg<>(    obj -> new DatabaseService((io.vertx.nms.agent.database.DatabaseService) obj),
    DatabaseService::getDelegate
  );

  private final io.vertx.nms.agent.database.DatabaseService delegate;
  
  public DatabaseService(io.vertx.nms.agent.database.DatabaseService delegate) {
    this.delegate = delegate;
  }

  public io.vertx.nms.agent.database.DatabaseService getDelegate() {
    return delegate;
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService fetchAllFaces(Handler<AsyncResult<List<JsonObject>>> resultHandler) { 
    delegate.fetchAllFaces(resultHandler);
    return this;
  }

  public Single<List<JsonObject>> rxFetchAllFaces() { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      fetchAllFaces(handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService fetchFaceById(int id, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.fetchFaceById(id, resultHandler);
    return this;
  }

  public Single<JsonObject> rxFetchFaceById(int id) { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      fetchFaceById(id, handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService createFace(int id, String remote, String local, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.createFace(id, remote, local, resultHandler);
    return this;
  }

  public Completable rxCreateFace(int id, String remote, String local) { 
    return io.vertx.reactivex.impl.AsyncResultCompletable.toCompletable(handler -> {
      createFace(id, remote, local, handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService saveFace(int id, String remote, String local, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.saveFace(id, remote, local, resultHandler);
    return this;
  }

  public Completable rxSaveFace(int id, String remote, String local) { 
    return io.vertx.reactivex.impl.AsyncResultCompletable.toCompletable(handler -> {
      saveFace(id, remote, local, handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService deleteFace(int id, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.deleteFace(id, resultHandler);
    return this;
  }

  public Completable rxDeleteFace(int id) { 
    return io.vertx.reactivex.impl.AsyncResultCompletable.toCompletable(handler -> {
      deleteFace(id, handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService deleteAllFaces(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.deleteAllFaces(resultHandler);
    return this;
  }

  public Completable rxDeleteAllFaces() { 
    return io.vertx.reactivex.impl.AsyncResultCompletable.toCompletable(handler -> {
      deleteAllFaces(handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService fetchAllFibEntries(Handler<AsyncResult<List<JsonObject>>> resultHandler) { 
    delegate.fetchAllFibEntries(resultHandler);
    return this;
  }

  public Single<List<JsonObject>> rxFetchAllFibEntries() { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      fetchAllFibEntries(handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService fetchFibEntry(String name, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.fetchFibEntry(name, resultHandler);
    return this;
  }

  public Single<JsonObject> rxFetchFibEntry(String name) { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      fetchFibEntry(name, handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService fetchFibEntryById(int id, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.fetchFibEntryById(id, resultHandler);
    return this;
  }

  public Single<JsonObject> rxFetchFibEntryById(int id) { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      fetchFibEntryById(id, handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService createFibEntry(String prefix, int faceId, int cost, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.createFibEntry(prefix, faceId, cost, resultHandler);
    return this;
  }

  public Completable rxCreateFibEntry(String prefix, int faceId, int cost) { 
    return io.vertx.reactivex.impl.AsyncResultCompletable.toCompletable(handler -> {
      createFibEntry(prefix, faceId, cost, handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService saveFibEntry(String prefix, int faceId, int cost, String id, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.saveFibEntry(prefix, faceId, cost, id, resultHandler);
    return this;
  }

  public Completable rxSaveFibEntry(String prefix, int faceId, int cost, String id) { 
    return io.vertx.reactivex.impl.AsyncResultCompletable.toCompletable(handler -> {
      saveFibEntry(prefix, faceId, cost, id, handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService deleteFibEntry(int entryId, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.deleteFibEntry(entryId, resultHandler);
    return this;
  }

  public Completable rxDeleteFibEntry(int entryId) { 
    return io.vertx.reactivex.impl.AsyncResultCompletable.toCompletable(handler -> {
      deleteFibEntry(entryId, handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService fetchAllLogs(Handler<AsyncResult<List<JsonObject>>> resultHandler) { 
    delegate.fetchAllLogs(resultHandler);
    return this;
  }

  public Single<List<JsonObject>> rxFetchAllLogs() { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      fetchAllLogs(handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService fetchLogById(int logId, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.fetchLogById(logId, resultHandler);
    return this;
  }

  public Single<JsonObject> rxFetchLogById(int logId) { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      fetchLogById(logId, handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService createLog(String timestamp, String verticle, String level, String message, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.createLog(timestamp, verticle, level, message, resultHandler);
    return this;
  }

  public Completable rxCreateLog(String timestamp, String verticle, String level, String message) { 
    return io.vertx.reactivex.impl.AsyncResultCompletable.toCompletable(handler -> {
      createLog(timestamp, verticle, level, message, handler);
    });
  }

  public io.vertx.nms.agent.database.reactivex.DatabaseService deleteLog(int logId, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.deleteLog(logId, resultHandler);
    return this;
  }

  public Completable rxDeleteLog(int logId) { 
    return io.vertx.reactivex.impl.AsyncResultCompletable.toCompletable(handler -> {
      deleteLog(logId, handler);
    });
  }


  public static  DatabaseService newInstance(io.vertx.nms.agent.database.DatabaseService arg) {
    return arg != null ? new DatabaseService(arg) : null;
  }
}
