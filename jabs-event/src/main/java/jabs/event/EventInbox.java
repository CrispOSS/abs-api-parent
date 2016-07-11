package jabs.event;

import jabs.AbstractInbox;
import jabs.Envelope;
import jabs.Opener;

/**
 * @author Behrooz Nobakht
 */
public class EventInbox extends AbstractInbox {

	private final Opener opener = new EventOpener();

	public EventInbox() {
	}

	@Override
	protected Opener opener(Envelope envelope, Object receiver) {
		return opener;
	}

}
