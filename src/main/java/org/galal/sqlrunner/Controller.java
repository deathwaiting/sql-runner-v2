package org.galal.sqlrunner;

import io.vertx.ext.web.RoutingContext;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.galal.sqlrunner.services.verticles.messages.SqlFilePathMsg;
import org.jboss.logging.Logger;

import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType.HTTP;
import static org.galal.sqlrunner.services.verticles.enums.Messages.EXECUTE_SQL;

@Path("/sql")
@SecurityScheme(securitySchemeName = "Basic Auth", type = HTTP, scheme = "basic")
public class Controller {

    private static final Logger LOG = Logger.getLogger(Controller.class);

    @Inject
    EventBus bus;

    @GET
    @Path("/{file}")
    public String runSql(@PathParam("file") String file, RoutingContext context) {
        var queryParams =
                stream(context.queryParams().spliterator(), false)
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        var msg = new SqlFilePathMsg(file, queryParams);
        return bus
                .request(EXECUTE_SQL, msg)
                .map(Message::body)
                .map(Object::toString)
                .onItem()
                .invoke(res -> LOG.debug("Returning Query result!"))
                .onFailure()
                .invoke(e -> LOG.error(format("Failed to run sql file [%s]!", file), e))
                .await().indefinitely();
    }
}
