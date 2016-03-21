package abs.api.remote.sample;


public class Main {

	public static void main(String[] args) throws Exception {

		int n = 100;
		if (args.length != 0) {
			n = Integer.parseInt(args[0]);
		}

		Master m = new Master(n);
		for (int i = 0; i < n; i++) {
			new Worker(i);
		}

		m.run_alg();

	}
}
