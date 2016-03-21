package abs.api.remote.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import abs.api.Actor;
import abs.api.Reference;
import abs.api.ReferenceFactory;
import abs.api.Response;

public class Worker extends AbstractNode implements Actor, Node {

	private static final long serialVersionUID = 1L;

	static final Reference MASTER = ReferenceFactory.DEFAULT
			.create("master@http://localhost:7777");

	final int UNRESOLVED = 0;
	final int HOLE = -1;

	private int workers, localIndex, actorIndex, d, num, kInit, graphArraySize,
			workerSize, aliveDelegates;
	private int arr[];
	private int initArr[];

	private Worker workerArray[];

	private int size;

	Random g = new Random(System.currentTimeMillis());
	int leader = -1;

	public Worker(int workers, int d, int num, int n, int size)
			throws Exception {
		this(n);
		this.workers = workers;
		this.d = d;
		this.num = num;
		this.size = size;

		arr = new int[size + 2];
		arr[0] = -3;

		initArr = new int[kInit + 2];
		initArr[0] = -3;

		initGlobals();
	}

	public Worker(int n) throws Exception {
		super(n);
		start();
	}

	public int localIndex(int index) {
		return (((index - 1) / d + 1 - (d + 2)) / workers) * d
				+ (index - 1) % d;
	}

	public int actorIndex(int index) {
		return ((index - 1) / d + 1 - (d + 2)) % workers;
	}

	public void initGlobals() {

		kInit = d * (d + 1);

		graphArraySize = num * d; // the size of the graph-array.
									// number-of-edges * 2

		workerSize = ((num) / workers) * d;

	}

	public void init() {

		// START INIT clique of the graph
		int index = 1;
		int i = 0; // i is for the 1st-loop
		int j = 0; // j is for the 2nd-loop
		while (i <= d) {
			j = 1;
			while (j <= d) {
				index = j + (i * d);
				if (j <= i)
					this.initArr[index] = j;
				else
					this.initArr[index] = HOLE;
				j = j + 1;
			}
			i = i + 1;
		}
		// END INIT the full clique of the graph
		// initialize the partition
		index = 0;
		while (index <= size) {
			arr[index] = UNRESOLVED;
			index++;
		}
	}

	public int request(int source) { // to return the requested slot of the
										// worker when it is resolved
		if (source > kInit) {
			int lSource = localIndex(source);
			await(self, () -> arr[lSource] != UNRESOLVED);
			return arr[lSource];
		} else
			return initArr[source];
	}

	public void delegate(Response<Integer> ft, int target) {

		await(self, () -> ft.getValue() != null);
		int u = ft.getValue();
		// START inlined conflictcheck
		boolean found = false;
		if (u != HOLE) {
			int i = ((target - 1) / d) * d + 1;
			int lCurrentNode = i + d - 1;
			int item = 0;
			int v = 0;

			while (i <= lCurrentNode) {
				int li = localIndex(i);
				if (arr[li] != UNRESOLVED)
					if (arr[li] == u) {
						found = true;
						i = lCurrentNode + 1; // just to break the while
					}

				i = i + 1;
			}
		}

		// END inlined conflictcheck
		if (found || u == HOLE) { // if conflict happens or it's an -1 from
									// initial clique
			u = ((target - 1) / d) * d;
			u = g.nextInt(u + 1) + 1;

			int aIndex = actorIndex(u);

			Worker w = workerArray[aIndex];

			final int tmp = u;
			Callable<Integer> c = () -> w.request(tmp);

			Response<Integer> fp = self.send(w, c);
			this.delegate(fp, target);

		} else { // the slot is resolved
			int lTarget = localIndex(target);
			arr[lTarget] = u;
			aliveDelegates = aliveDelegates - 1;
		}
	}

	public void run_() {
		int j = 0;
		int i = d + 2 + (id - 1);
		int temp = kInit + (id - 1) * d;
		int source = 0;
		int target = 0;
		int u = 0;
		while (i <= num) {
			j = 1;
			List<Integer> pastDraws = new ArrayList<Integer>();
			while (j <= d) {
				source = g.nextInt(temp * 2 + 1) + 1;
				target = temp + j;
				u = 0;
				if (source > temp) { // this is for the coin flipping, it means
										// that you pick from the shadow array
					source = source - temp;
					if (source > kInit) {
						u = (source - 1) / d + 1;
						if (pastDraws.contains(u))
							j = j - 1;
						else {
							pastDraws.add(u);
							int lTarget = localIndex(target);
							arr[lTarget] = u;
						}
					} else { // the picked element is from the shadow-clique
								// array
						u = initArr[source];
						if (u == HOLE) // picked an empty element of initial
										// clique
							j = j - 1; // so retry
						else {
							u = (source - 1) / d + 1;
							if (pastDraws.contains(u))
								j = j - 1;
							else {
								pastDraws.add(u);
								int lTarget = localIndex(target);
								arr[lTarget] = u;
							}
						}
					}
				} else // here you pick from the real array
				{
					if (source > kInit) {
						int aIndex = actorIndex(source);
						Worker w = workerArray[aIndex];

						final int tmp = source;
						Callable<Integer> cd = () -> w.request(tmp);

						Response<Integer> fp = self.send(w, cd);
						final int tmpTarget = target;

						Runnable cdel = () -> this.delegate(fp, tmpTarget);

						self.send(self, cdel);
						aliveDelegates = aliveDelegates + 1;
					} else {

						u = initArr[source];

						if (u == HOLE) // picked an empty element of
												// initial clique
							j = j - 1;
						else if (pastDraws.contains(u))
							j = j - 1;
						else {
							pastDraws.add(u);
							int lTarget = localIndex(target);
							arr[lTarget] = u;
						}
					}
				}
				j = j + 1;
			}
			i = i + workers;
			temp = temp + d * workers;
		}
		// one while loops to wait for all delegates to finish
		await(self, () -> this.aliveDelegates == 0);

	}
}
