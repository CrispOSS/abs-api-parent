package abs.api.remote;

import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

import javax.ws.rs.core.Application;

import jabs.Configuration;
import jabs.Context;
import jabs.LocalContext;
import jabs.LocalRouter;
import jabs.ReferenceFactory;
import jabs.Router;
import jabs.SystemContext;

/**
 * @author Behrooz Nobakht
 * @since 1.0
 */
public class ContextApplication extends Application {

  private static final int MAX_LOCAL_ACTORS = 1024 * 1024;

  public final Context context;

  private final SystemContext systemContext;
  private final ContextResource contextResource;

  public ContextApplication(URI uri, ConcurrentMap<Class, MessageConverter> paramConverters,
      ConcurrentMap<Class, BiConsumer> messageConsumers) {
    final ReferenceFactory referenceFactory = new RemoteReferenceFactory(uri);
    final Router localRouter = new LocalRouter();
    final Router remoteRouter = new RemoteRouter(uri);
    final CompositeRouter router = new CompositeRouter(uri, localRouter, remoteRouter);
    final Configuration config = Configuration.newConfiguration()
        .withReferenceFactory(referenceFactory).withEnvelopeRouter(router)
        // .withInbox(new
        // DispatchInbox(Executors.newWorkStealingPool()))
        .build();
    this.context = new LocalContext(config);
    localRouter.bind(context);
    remoteRouter.bind(context);

    this.systemContext = new SystemContext();
    this.systemContext.bind(context);

    this.contextResource = new ContextResource(context, uri,
        Integer.getInteger("maxLocalActors", MAX_LOCAL_ACTORS), paramConverters, messageConsumers);

  }

  @Override
  public Set<Object> getSingletons() {
    return Collections.singleton(contextResource);
  }

}
