package abs.api.remote.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import abs.api.Actor;
import abs.api.Reference;
import abs.api.ReferenceFactory;
import abs.api.Response;

public class Master extends AbstractNode implements Actor, Node {

	private static final long serialVersionUID = 1L;

	private List<Integer> nodes;
	private final int workers, num, d, i = 0;
	private int size;
	private int off;

	private String workerArray[];
	private List<Response<Integer>> futures = new ArrayList<Response<Integer>>();

	public Master(int workers, int num, int d) throws Exception {
		super(workers);
		this.workers = workers;
		this.d = d;
		this.num = num;
		this.nodes = new ArrayList<>(workers);
		size = (num - (d + 1)) / workers;
		off = num - (d + 1) - size * workers;
		start();
	}

	public void init(String workerArray[]) {
		this.workerArray = workerArray;
	}

	@Override
	public int getPort() {
		return 7777;
	}

	@Override
	public String getName() {
		return "master";
	}

	public void workerReady(int id) {
		nodes.add(id);
		if (nodes.size() == workers) {
			for (Integer integer : nodes) {
				Reference wRef = ReferenceFactory.DEFAULT.create(workerArray[integer]);
				Worker w = (Worker) wRef;

				Callable<Integer> runCall = () -> w.run_();
				futures.add(self.send(w, runCall));
			}
			
			for (Response<Integer> response : futures) {
				response.getValue();
			}
		}
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
