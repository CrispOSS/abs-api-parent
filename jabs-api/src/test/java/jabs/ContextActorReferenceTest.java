package jabs;

import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.api.Assertions.assertNotNull;

import org.junit.gen5.api.Test;

import jabs.AbstractInbox;
import jabs.Actor;
import jabs.Configuration;
import jabs.Context;
import jabs.Envelope;
import jabs.LocalContext;
import jabs.MethodReference;
import jabs.SimpleEnvelope;

/**
 * 
 * @author Behrooz Nobakht
 * @since 1.0
 */
public class ContextActorReferenceTest {

  static {
    System.setProperty(Configuration.PROPERTY_THREAD_MANAGEMENT, "false");
  }

  @Test
  public void beforeTheFirstMessageNoSenderIsAvailable() throws Exception {
    Context context = new LocalContext();
    Actor b = context.newActor("b", new Object());
    assertNotNull(b.senderReference());
    assertEquals(Actor.NOBODY, b.senderReference());
  }

  @Test
  public void afterTheFirstMessageTheSenderRemainsAsTheLastSender() throws Exception {
    Context context = new LocalContext();
    Object objA = new Object();
    Object objB = new Object();
    Actor a = context.newActor("a", objA);
    Actor b = context.newActor("b", objB);
    MethodReference method = MethodReference.of(b, "hashcode");
    assertEquals(Actor.NOBODY, b.senderReference());

    Envelope envelope =
        new SimpleEnvelope(context.reference(objA), context.reference(objB), method);
    AbstractInbox inbox = new AbstractInbox();
    inbox.bind(context);
    inbox.onOpen(envelope, null, null);
    assertEquals(a.name(), b.senderReference().name());
  }

}