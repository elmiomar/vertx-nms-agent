package io.vertx.nms.agent.nfd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.nms.agent.common.BaseVerticle;

public class NfdVerticle extends BaseVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        EventBus eb = vertx.eventBus();

        // Send a message every second

        vertx.setPeriodic(3000, v -> {
            // MonitorMessage msg = new MonitorMessage(SenderVerticle.class.getName(), "some
            // randome message", getTimestamp());
            System.out.println("SenderVerticle sending...");

            sendLogEvent("nms.web.monitor",
                    new JsonObject().put("timestamp", getTimestamp()).put("verticle", NfdVerticle.class.getName())
                            .put("level", "INFO").put("message", "testing monitor message"),
                    reply -> {
                        if (reply.succeeded()) {
                            System.out.println("Received reply " + reply.result().body());
                        } else {
                            // System.out.println("No reply");
                        }
                    });

        });
    }

    private String getTimestamp() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        return strDate;
    }
}