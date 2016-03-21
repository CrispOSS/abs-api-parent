package abs.api.remote.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import abs.api.Actor;
import abs.api.Reference;
import abs.api.ReferenceFactory;
import abs.api.Response;

public class Master extends AbstractNode implements Actor, Node {

	private static final long serialVersionUID = 1L;

	private List<Integer> nodes;
	private final int workers, num=10000, d=3, i=1;
	private int size;
	private int off;

	public Master(int workers) throws Exception {
		super(0);
		this.workers = workers;
		this.nodes = new ArrayList<>(workers);
		size = (num - (d+1)) / workers;
		off = num - (d+1) - size * workers;
		
		start();
	}

	@Override
	public int getPort() {
		return 7777;
	}

	@Override
	public String getName() {
		return "master";
	}

	
	
	private int count_max() {
		Set<Integer> keys = new HashSet<Integer>();
		int max = 0;
		for (Integer integer : keys) {
			if (max < Collections.frequency(nodes, integer)) {
				max = integer;
			}
		}
		return max;
	}

	public void run_alg() {
		Set<Response<Integer>> workFutures = new HashSet<>();
		for (int i = 0; i < workers; i++) {
			Reference nRef = getNodeReference(i);
			// Worker nNode = (Worker) object(nRef);
			// Runnable msg = () -> nNode.selectLeader(new
			// Integer(workers));
			Response<Integer> r = self.send(nRef, new Integer(workers));
			workFutures.add(r);
			logger.info("msg sent to node: {}", nRef);
		}

		workFutures.forEach(f -> {
			try {
				Integer result = f.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		workFutures.clear();

		show();

	}

	public void show() {
		logger.info("Leader= {}", count_max());
	}

	protected Reference getNodeReference(int n) {
		int port = DEFAULT_PORT_START + n;
		return ReferenceFactory.DEFAULT.create("node-" + n + "@http://localhost:" + port);
	}
}
