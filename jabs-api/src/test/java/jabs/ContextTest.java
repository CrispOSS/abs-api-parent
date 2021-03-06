package jabs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * @author Behrooz Nobakht
 */
public class ContextTest {

  static class MyActor implements Actor {
    private static final long serialVersionUID = 1L;

    public Double doIt(Integer x) {
      return Math.random() * x;
    }
  }

  @Test
  public void testSendMessageOutsideActor() throws Exception {
    Configuration config = Configuration.newConfiguration().disableThreadManagement()
        .withInbox(new AsyncInbox()).build();
    LocalContext context = new LocalContext(config);
    final MyActor actor = new MyActor();
    context.newActor("myActor", actor);
    Callable<Double> message = () -> actor.doIt(10);
    Future<?> result = context.send(actor, message);
    assertNotNull(result);
    assertNotNull(result.get());
    assertEquals(Double.class, result.get().getClass());
  }

}
