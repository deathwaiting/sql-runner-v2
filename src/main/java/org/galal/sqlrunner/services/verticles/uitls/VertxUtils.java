package org.galal.sqlrunner.services.verticles.uitls;

import io.vertx.mutiny.core.eventbus.Message;
import org.galal.sqlrunner.utils.Utils;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static java.util.Optional.ofNullable;
import static org.galal.sqlrunner.services.verticles.enums.Headers.STATUS;

public class VertxUtils {

    public static Integer readMessageStatus(Message<?> message){
       return ofNullable(message.headers().get(STATUS.name()))
                .flatMap(Utils::safeIntParsing)
                .orElse(OK.code());
    }
}
