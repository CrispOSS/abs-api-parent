package abs.api.remote.sample;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.api.Actor;
import abs.api.remote.ActorServer;

/**
 * @author Behrooz Nobakht
 */
public abstract class AbstractNode implements Node {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final int id;
	protected ActorServer server;
	protected Actor self;

	public AbstractNode(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	protected Properties getProperties() {
		Properties p = new Properties();
		p.setProperty("host", "localhost");
		p.setProperty("port", String.valueOf(getPort()));
		return p;
	}

	protected void start() throws Exception {
		if (self == null) {
			server = new ActorServer(getProperties());
			self = server.context.newActor(getName(), this);
		}
	}

}
