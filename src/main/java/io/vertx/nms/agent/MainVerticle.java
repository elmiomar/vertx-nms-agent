package io.vertx.nms.agent;

import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Single<String> dbVerticleDeployment = vertx.rxDeployVerticle("io.vertx.nms.agent.database.DatabaseVerticle");

    DeploymentOptions opts = new DeploymentOptions().setInstances(1);
    dbVerticleDeployment
      .flatMap(id -> vertx.rxDeployVerticle("io.vertx.nms.agent.http.HttpServerVerticle", opts))
      .subscribe(id -> startFuture.complete(), startFuture::fail);
  }
}
