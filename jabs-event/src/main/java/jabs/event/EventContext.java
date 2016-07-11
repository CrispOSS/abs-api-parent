package jabs.event;

import jabs.Configuration;
import jabs.LocalContext;

/**
 * @author Behrooz Nobakht
 * @since 1.0
 */
public class EventContext extends LocalContext {

	public EventContext() {
		super(Configuration.newConfiguration().withEnvelopeOpener(new EventOpener())
				.withInbox(new EventInbox()).build());
	}

}
