package io.vertx.nms.agent.common;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class RxBaseVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start();
    }

    protected void publishLogEvent(String address, String vertcile, String level, String message) {
        vertx.eventBus().publish(address, new JsonObject().put("timestamp", getTimestamp()).put("verticle", vertcile)
                .put("level", level).put("message", message));
    }

    private String getTimestamp() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        return strDate;
    }

}