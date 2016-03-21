package abs.api.remote.sample;

import java.util.Properties;

import abs.api.Actor;
import abs.api.Reference;
import abs.api.remote.ActorServer;

public class MainWorker {

	public static void main(String[] args) throws Exception {

		String location ="@http://localhost:";
		
		int n = Integer.parseInt(args[0]);
		int size = Integer.parseInt(args[1]);
		int d = Integer.parseInt(args[2]);
		int workers = Integer.parseInt(args[3]);
		int num = Integer.parseInt(args[4]);
		
		Worker me = new Worker(workers, d, num, n, size);
		
		
		
		String workerArray[] = new String[workers+1];
		for (int i = 0; i < workers; i++) {
			Node nodeId;
			if(i==n)
				workerArray[i]=me.getName()+location+me.getPort();
			else{
				nodeId = new Node() {
					
					@Override
					public int getId() {
						// TODO Auto-generated method stub
						return i;
					}
				};
				workerArray[i] = nodeId.getName()+location+nodeId.getPort();
			}
		}	
		
		me.init(workerArray);
	}
}
