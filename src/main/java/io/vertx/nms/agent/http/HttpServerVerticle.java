
package io.vertx.nms.agent.http;

import io.reactivex.Flowable;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.nms.agent.common.RxBaseVerticle;
import io.vertx.nms.agent.database.DatabaseVerticle;
import io.vertx.nms.agent.database.reactivex.DatabaseService;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CookieHandler;
import io.vertx.reactivex.ext.web.handler.CorsHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HttpServerVerticle extends RxBaseVerticle {

  public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
  public static final String CONFIG_DB_QUEUE = "db.queue";
  public static final String VERTICLE_NAME = HttpServerVerticle.class.getName();

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  private DatabaseService dbService;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    String dbQueue = config().getString(CONFIG_DB_QUEUE, "db.queue");
    dbService = io.vertx.nms.agent.database.DatabaseService.createProxy(vertx.getDelegate(), dbQueue);

    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);

    Set<String> allowedHeaders = new HashSet<>();
    allowedHeaders.add("x-requested-with");
    allowedHeaders.add("Access-Control-Allow-Origin");
    allowedHeaders.add("origin");
    allowedHeaders.add("Content-Type");
    allowedHeaders.add("accept");
    allowedHeaders.add("X-PINGARUNER");

    Set<HttpMethod> allowedMethods = new HashSet<>();
    allowedMethods.add(HttpMethod.GET);
    allowedMethods.add(HttpMethod.POST);
    allowedMethods.add(HttpMethod.OPTIONS);
    allowedMethods.add(HttpMethod.DELETE);
    allowedMethods.add(HttpMethod.PATCH);
    allowedMethods.add(HttpMethod.PUT);

    CorsHandler corsHandler = CorsHandler.create("http://localhost:8000").allowedHeaders(allowedHeaders)
        .allowCredentials(true);
    Arrays.asList(HttpMethod.values()).stream().forEach(method -> corsHandler.allowedMethod(method));
    router.route().handler(corsHandler);

    router.get("/access-control-with-get").handler(ctx -> {
      HttpServerResponse httpServerResponse = ctx.response();
      httpServerResponse.setChunked(true);
      MultiMap headers = ctx.request().headers();
      for (String key : headers.names()) {
        httpServerResponse.write(key + ": ");
        httpServerResponse.write(headers.get(key));
        httpServerResponse.write("<br>");
      }
      httpServerResponse.putHeader("Content-Type", "application/text").end("Success");
    });

    router.post("/access-control-with-post-preflight").handler(ctx -> {
      HttpServerResponse httpServerResponse = ctx.response();
      httpServerResponse.setChunked(true);
      MultiMap headers = ctx.request().headers();
      for (String key : headers.names()) {
        httpServerResponse.write(key + ": ");
        httpServerResponse.write(headers.get(key));
        httpServerResponse.write("<br>");
      }
      httpServerResponse.putHeader("Content-Type", "application/text").end("Success");
    });

    router.route().handler(CookieHandler.create());
    router.route().handler(BodyHandler.create());
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

    // sockjs-handler-setup
    SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
    BridgeOptions bridgeOptions = new BridgeOptions()
        .addInboundPermitted(new PermittedOptions().setAddress("nms.web.monitor"))
        .addOutboundPermitted(new PermittedOptions().setAddress("nms.web.monitor"));
    sockJSHandler.bridge(bridgeOptions);
    router.route("/eventbus/*").handler(sockJSHandler);
    // sockjs-handler-setup

    // eventbus-consumer
    vertx.eventBus().<String>consumer("app.markdown", msg -> {

    });
    // eventbus-consumer

    router.get("/app/*").handler(StaticHandler.create().setCachingEnabled(false));
    router.get("/").handler(context -> context.reroute("/app/index.html"));

    // faces api
    router.get("/api/faces").handler(this::apiGetAllFaces);
    router.get("/api/faces/:id").handler(this::apiGetFace);
    router.post().handler(BodyHandler.create());
    router.post("/api/faces").handler(this::apiCreateFace);
    router.put().handler(BodyHandler.create());
    router.put("/api/faces/:faceId").handler(this::apiUpdateFace);
    router.delete("/api/faces/:faceId").handler(this::apiDeleteFace);
    router.delete("/api/faces").handler(this::apiDeleteAllFaces);

    // fib api
    router.get("/api/fib").handler(this::apiGetAllFib);
    router.get("/api/fib/:entryId").handler(this::apiGetFibEntry);
    router.post("/api/fib").handler(this::apiCreateFibEntry);
    router.put("/api/fib/:entryId").handler(this::apiUpdateFibEntry);
    router.delete("/api/fib/:entryId").handler(this::apiDeleteFibEntry);

    // logs api
    router.get("/api/logs").handler(this::apiGetAllLogs);
    router.get("/api/logs/:logId").handler(this::apiGetLogById);
    router.post("/api/logs").handler(this::apiCreateLog);
    router.delete("/api/logs/:logId").handler(this::apiDeleteLog);
    router.delete("/api/logs").handler(this::apiDeleteAllLogs);

    int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
    server.requestHandler(router).rxListen(portNumber).subscribe(s -> {
      LOGGER.info("HTTP server running on port " + portNumber);
      startFuture.complete();
    }, t -> {
      LOGGER.error("Could not start a HTTP server", t);
      startFuture.fail(t);
    });
  }

  private void apiCreateFace(RoutingContext context) {
    LOGGER.info("[API] Create Face");
    publishLogEvent("nms.web.monitor", HttpServerVerticle.class.getSimpleName(), "INFO",
        "request to create a new face");
    JsonObject face = context.getBodyAsJson();
    if (!validateJsonFaceDocument(context, face, "remoteUri", "localUri")) {
      return;
    }
    dbService.rxCreateFace(face.getInteger("faceId"), face.getString("remoteUri"), face.getString("localUri"))
        .doOnComplete(() -> {
          publishLogEvent("nms.web.monitor", HttpServerVerticle.class.getSimpleName(), "INFO",
              "face with id=" + face.getInteger("faceId") + " was created succesfully!");

        }).subscribe(
            () -> apiResponse(context, 201, "message",
                "face with id=" + face.getInteger("faceId") + " was created succesfully!"),
            t -> apiFailure(context, t));
  }

  private void apiGetAllFaces(RoutingContext context) {
    LOGGER.debug("[API] Get all faces, request url: " + context.request().absoluteURI());
    publishLogEvent("nms.web.monitor", HttpServerVerticle.class.getSimpleName(), "INFO",
        "request to retrieve all faces");
    dbService.rxFetchAllFaces().doOnSuccess(
        ok -> publishLogEvent("nms.web.monitor", DatabaseVerticle.class.getSimpleName(), "INFO", "retrieved all faces"))
        .flatMapPublisher(Flowable::fromIterable)
        .map(obj -> new JsonObject().put("faceId", obj.getInteger("ID")).put("remoteUri", obj.getString("REMOTE"))
            .put("localUri", obj.getString("LOCAL")))
        .collect(JsonArray::new, JsonArray::add)
        .subscribe(faces -> apiResponse(context, 200, "faces", faces), t -> apiFailure(context, t));
  }

  private void apiGetFace(RoutingContext context) {
    LOGGER.debug("[API] Get Face, request url: " + context.request().getParam("id"));
    int id = Integer.valueOf(context.request().getParam("id"));
    publishLogEvent("nms.web.monitor", HttpServerVerticle.class.getSimpleName(), "INFO",
        "retrieving face with id=" + id);
    dbService.rxFetchFaceById(id).subscribe(dbObject -> {
      if (dbObject.getBoolean("found")) {
        LOGGER.debug("[API] Get Face =" + context.request().getParam("id"));
        publishLogEvent("nms.web.monitor", DatabaseVerticle.class.getSimpleName(), "INFO",
            "retrieved face with id=" + id);
        JsonObject payload = new JsonObject().put("id", dbObject.getInteger("id"))
            .put("remoteUri", dbObject.getString("remote")).put("localUri", dbObject.getString("local"));
        apiResponse(context, 200, "face", payload);
      } else {
        apiFailure(context, 404, "There is no face with ID " + id);
      }
    }, t -> apiFailure(context, t));
  }

  private void apiDeleteFace(RoutingContext context) {
    int id = Integer.valueOf(context.request().getParam("faceId"));
    publishLogEvent("nms.web.monitor", HttpServerVerticle.class.getSimpleName(), "INFO",
        "request to delete face with id=" + id);
    dbService.rxDeleteFace(id).doOnComplete(() -> {
      publishLogEvent("nms.web.monitor", DatabaseVerticle.class.getSimpleName(), "INFO",
          "deleted face with id=" + id + " successfully!");
    }).subscribe(() -> apiResponse(context, 200, null, null), t -> apiFailure(context, t));
  }

  private void apiDeleteAllFaces(RoutingContext context) {
    dbService.rxDeleteAllFaces().doOnComplete(() -> {
      publishLogEvent("nms.web.monitor", DatabaseVerticle.class.getSimpleName(), "INFO", "deleted all faces");
    }).subscribe(() -> apiResponse(context, 200, null, null), t -> apiFailure(context, t));
  }

  private void apiUpdateFace(RoutingContext context) {
    int id = Integer.valueOf(context.request().getParam("faceId"));
    publishLogEvent("nms.web.monitor", HttpServerVerticle.class.getSimpleName(), "INFO",
        "request to update face with id=" + id);
    JsonObject face = context.getBodyAsJson();
    if (!validateJsonFaceDocument(context, face, "remoteUri", "localUri")) {
      return;
    }
    // publish-on-page-updated
    dbService.rxSaveFace(id, face.getString("remoteUri"), face.getString("localUri")).doOnComplete(() -> {
      JsonObject event = new JsonObject().put("id", id).put("remoteUri", face.getString("remoteUri")).put("localUri",
          face.getString("localUri"));
      publishLogEvent("nms.web.monitor", DatabaseVerticle.class.getSimpleName(), "INFO",
          "updated face with id=" + id + " successfully!");
      vertx.eventBus().publish("page.saved", event);
    }).subscribe(() -> apiResponse(context, 200, null, null), t -> apiFailure(context, t));
    // publish-on-page-updated
  }

  private boolean validateJsonFaceDocument(RoutingContext context, JsonObject face, String... expectedKeys) {
    if (!Arrays.stream(expectedKeys).allMatch(face::containsKey)) {
      LOGGER.error(
          "bad face creation JSON payload: " + face.encodePrettily() + " from " + context.request().remoteAddress());
      publishLogEvent("nms.web.monitor", HttpServerVerticle.class.getSimpleName(), "ERROR",
          "bad face creation JSON payload");
      context.response().setStatusCode(400);
      context.response().putHeader("Content-Type", "application/json");
      context.response().end(new JsonObject().put("success", false).put("error", "bad request payload").encode());
      return false;
    }
    return true;
  }

  // fib related methods

  private void apiCreateFibEntry(RoutingContext context) {

    JsonObject entry = context.getBodyAsJson();
    LOGGER.debug("[API] Create Fib Entry: " + entry);
    publishLogEvent("nms.web.monitor", HttpServerVerticle.class.getSimpleName(), "INFO",
        "request to create new fib entry for prefix " + entry.getString("prefix"));
    if (!validateJsonFibEntryDocument(context, entry, "prefix", "faceId", "cost")) {
      return;
    }
    dbService.rxCreateFibEntry(entry.getString("prefix"), entry.getInteger("faceId"), entry.getInteger("cost"))
        .doOnComplete(() -> {
          publishLogEvent("nms.web.monitor", DatabaseVerticle.class.getSimpleName(), "INFO", "created new fib entry");
        }).subscribe(
            () -> apiResponse(context, 201, "message",
                "fib entry for prefix " + entry.getString("prefix") + " was created succesfully!"),
            t -> apiFailure(context, t));
  }

  private void apiGetFibEntry(RoutingContext context) {
    int id = Integer.valueOf(context.request().getParam("entryId"));
    LOGGER.debug("[API] Get one fib entry: " + id);
    dbService.rxFetchFibEntryById(id).subscribe(dbObject -> {
      if (dbObject.getBoolean("found")) {
        JsonObject payload = new JsonObject().put("entryId", dbObject.getInteger("id"))
            .put("prefix", dbObject.getString("prefix")).put("face", dbObject.getInteger("face"))
            .put("cost", dbObject.getInteger("cost"));
        apiResponse(context, 200, "entry", payload);
      } else {
        apiFailure(context, 404, "There is no fib entry with ID " + id);
      }
    }, t -> apiFailure(context, t));
  }

  private void apiDeleteFibEntry(RoutingContext context) {
    int id = Integer.valueOf(context.request().getParam("entryId"));
    dbService.rxDeleteFibEntry(id).doOnComplete(() -> {
      publishLogEvent("nms.web.monitor", DatabaseVerticle.class.getSimpleName(), "INFO",
          "deleted fib entry with id=" + id);
    }).subscribe(() -> apiResponse(context, 200, "message", "fib entry was deleted succesfully!"),
        t -> apiFailure(context, t));
  }

  private void apiUpdateFibEntry(RoutingContext context) {
    int id = Integer.valueOf(context.request().getParam("faceId"));
    JsonObject face = context.getBodyAsJson();
    if (!validateJsonFaceDocument(context, face, "remoteUri", "localUri")) {
      return;
    }
    // publish-on-page-updated
    dbService.rxSaveFace(id, face.getString("remoteUri"), face.getString("localUri")).doOnComplete(() -> {
      JsonObject event = new JsonObject().put("id", id).put("remoteUri", face.getString("remoteUri")).put("localUri",
          face.getString("localUri"));
      publishLogEvent("nms.web.monitor", DatabaseVerticle.class.getSimpleName(), "INFO",
          "updated face with id=" + id + "succesfully!");
    }).subscribe(() -> apiResponse(context, 200, null, null), t -> apiFailure(context, t));
    // publish-on-page-updated
  }

  private boolean validateJsonFibEntryDocument(RoutingContext context, JsonObject entry, String... expectedKeys) {
    if (!Arrays.stream(expectedKeys).allMatch(entry::containsKey)) {
      LOGGER.error(
          "bad fib creation JSON payload: " + entry.encodePrettily() + " from " + context.request().remoteAddress());
      publishLogEvent("nms.web.monitor", HttpServerVerticle.class.getSimpleName(), "ERROR",
          "bad fib creation JSON payload");
      context.response().setStatusCode(400);
      context.response().putHeader("Content-Type", "application/json");
      context.response().end(new JsonObject().put("success", false).put("error", "bad request payload").encode());
      return false;
    }
    return true;
  }

  private void apiGetAllFib(RoutingContext context) {
    LOGGER.debug("[API] Get all fib entries, request url: " + context.request().absoluteURI());
    publishLogEvent("nms.web.monitor", HttpServerVerticle.class.getSimpleName(), "INFO", "retrieving all fib entries");
    dbService.rxFetchAllFibEntries().flatMapPublisher(Flowable::fromIterable)
        .map(obj -> new JsonObject().put("id", obj.getInteger("ID")).put("prefix", obj.getString("PREFIX"))
            .put("faceId", obj.getInteger("FACE")).put("cost", obj.getInteger("COST")))
        .collect(JsonArray::new, JsonArray::add)
        .subscribe(fib -> apiResponse(context, 200, "fib", fib), t -> apiFailure(context, t));
  }

  // Logs api functions

  private boolean validateJsonLogDocument(RoutingContext context, JsonObject entry, String... expectedKeys) {
    if (!Arrays.stream(expectedKeys).allMatch(entry::containsKey)) {
      LOGGER.error(
          "bad log creation JSON payload: " + entry.encodePrettily() + " from " + context.request().remoteAddress());
      context.response().setStatusCode(400);
      context.response().putHeader("Content-Type", "application/json");
      context.response().end(new JsonObject().put("success", false).put("error", "bad request payload").encode());
      return false;
    }
    return true;
  }

  private void apiGetAllLogs(RoutingContext context) {
    LOGGER.debug("[API] Get all logs, request url: " + context.request().absoluteURI());
    dbService.rxFetchAllLogs().flatMapPublisher(Flowable::fromIterable)
        .map(obj -> new JsonObject().put("id", obj.getInteger("ID")).put("timestamp", obj.getString("TIMESTAMP"))
            .put("verticle", obj.getString("VERTICLE")).put("level", obj.getString("LEVEL"))
            .put("message", obj.getString("MESSAGE")))
        .collect(JsonArray::new, JsonArray::add)
        .subscribe(logs -> apiResponse(context, 200, "logs", logs), t -> apiFailure(context, t));
  }

  private void apiGetLogById(RoutingContext context) {
    int id = Integer.valueOf(context.request().getParam("logId"));
    LOGGER.debug("[API] Get one log entry: " + id);
    dbService.rxFetchLogById(id).subscribe(dbObject -> {
      if (dbObject.getBoolean("found")) {
        JsonObject payload = new JsonObject().put("logId", dbObject.getInteger("id"))
            .put("timestamp", dbObject.getString("timestamp")).put("verticle", dbObject.getString("verticle"))
            .put("level", dbObject.getString("level")).put("message", dbObject.getString("message"));
        apiResponse(context, 200, "log", payload);
      } else {
        apiFailure(context, 404, "There is no log entry with ID " + id);
      }
    }, t -> apiFailure(context, t));
  }

  private void apiCreateLog(RoutingContext context) {
    JsonObject log = context.getBodyAsJson();
    LOGGER.info("[API] Create log " + log);
    if (!validateJsonLogDocument(context, log, "timestamp", "verticle", "level", "message")) {
      return;
    }
    dbService
        .rxCreateLog(log.getString("timestamp"), log.getString("verticle"), log.getString("level"),
            log.getString("message"))
        .subscribe(() -> apiResponse(context, 201, "message", "log record was created succesfully!"),
            t -> apiFailure(context, t));
  }

  private void apiDeleteLog(RoutingContext context) {
    int id = Integer.valueOf(context.request().getParam("logId"));
    dbService.rxDeleteLog(id).subscribe(
        () -> apiResponse(context, 200, "message", "log record was deleted succesfully!"), t -> apiFailure(context, t));
  }

  private void apiDeleteAllLogs(RoutingContext context) {
    dbService.rxDeleteAllLogs().subscribe(() -> apiResponse(context, 200, null, null), t -> apiFailure(context, t));
  }

  private void apiResponse(RoutingContext context, int statusCode, String jsonField, Object jsonData) {
    context.response().setStatusCode(statusCode);
    context.response().putHeader("Content-Type", "application/json");
    JsonObject wrapped = new JsonObject().put("success", true);
    if (jsonField != null && jsonData != null) {
      wrapped.put(jsonField, jsonData);
    }
    context.response().end(wrapped.encode());
  }

  private void apiFailure(RoutingContext context, Throwable t) {
    apiFailure(context, 500, t.getMessage());
  }

  private void apiFailure(RoutingContext context, int statusCode, String error) {
    context.response().setStatusCode(statusCode);
    context.response().putHeader("Content-Type", "application/json");
    context.response().end(new JsonObject().put("success", false).put("error", error).encode());
  }
}
