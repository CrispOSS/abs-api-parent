package abs.api.remote.sample;

import java.util.Properties;

import abs.api.Actor;
import abs.api.Reference;
import abs.api.remote.ActorServer;

public class MainMaster {

	public static void main(String[] args) throws Exception {

		String location = "@http://localhost:";

		int n = Integer.parseInt(args[0]);
		int size = Integer.parseInt(args[1]);
		int d = Integer.parseInt(args[2]);
		int workers = Integer.parseInt(args[3]);
		int num = Integer.parseInt(args[4]);

		
		String workerArray[] = new String[workers + 1];
		for (int i = 0; i < workers; i++) {

			final int id = i;
			Node nodeId = new Node() {

				@Override
				public int getId() {
					// TODO Auto-generated method stub
					return id;
				}
			};
			workerArray[i] = nodeId.getName() + location + nodeId.getPort();
		}
		Master m = new Master(workers, num, d);
		
	}
}
