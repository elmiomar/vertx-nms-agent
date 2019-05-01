package io.vertx.nms.agent.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public abstract class BaseVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start();
    }

    protected void publishLogEvent(String address, String timestamp, String vertcile, String level, String message) {
        vertx.eventBus().publish(address, new JsonObject().put("timestamp", getTimestamp()).put("vertcile", vertcile)
                .put("level", level).put("message", message));
    }

    protected void sendLogEvent(String address, JsonObject message, Handler<AsyncResult<Message<Object>>> replyHandler) {
        vertx.eventBus().send(address, message, replyHandler);
    }

    private String getTimestamp() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        return strDate;
    }

}