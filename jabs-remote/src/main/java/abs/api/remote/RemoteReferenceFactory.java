package abs.api.remote;

import java.net.URI;

import jabs.Reference;
import jabs.ReferenceFactory;

/**
 * @author Behrooz Nobakht
 * @since 1.0
 */
public class RemoteReferenceFactory implements ReferenceFactory {

	private final URI uri;

	public RemoteReferenceFactory(URI uri) {
		this.uri = uri;
	}

	@Override
	public Reference create(String name) {
		String uri = DEFAULT.create(name).name().toASCIIString();
		uri = uri + "@" + this.uri.toASCIIString();
		return Reference.from(uri);
	}

}
