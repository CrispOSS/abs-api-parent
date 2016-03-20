package abs.api.remote.sample;

/**
 * @author Behrooz Nobakht
 */
public interface Node {

	int DEFAULT_PORT_START = 1888;

	int getId();

	default int getPort() {
		return DEFAULT_PORT_START + getId();
	}

	default String getName() {
		return "node-" + getId();
	}

}
