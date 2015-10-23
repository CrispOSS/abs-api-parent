package abs.api;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;



public class CloudProvider {

	Client oneClient;
	String sessionString;

	public CloudProvider() {
		// TODO Auto-generated constructor stub
		try {
			oneClient = new Client("envisage-vlads:b1neRmTicnh$",
					"http://one-xmlrpc.calligo.sara.nl:2633/RPC2");
			sessionString = "envisage-vlads:b1neRmTicnh$";
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public boolean acquireInstance(DeploymentComponent instance){
	  return true;
	}

	public boolean killInstance(DeploymentComponent instance){
	  instance.vm.shutdown();
	  instance.vm.delete();
	  return true;
	}
	
	public boolean releaseInstance(DeploymentComponent instance){
      instance.vm.shutdown();
      instance.vm.delete();
      return true;
    }
	
	public VirtualMachine launchInstance(int tid, String vname) {

		try {

			OneResponse rc;
			Boolean success;
			Integer vid=0, errcode=0;

			rc = oneClient.call("one.template.instantiate", sessionString, tid, vname);

			if (rc.isError()) {
				System.out.println("failed!");
				throw new Exception(rc.getErrorMessage());
			}

			System.out.println(rc.getMessage());
			
			// The response message is the new VM's ID
			int newVMID = Integer.parseInt(rc.getMessage());
			System.out.println("ok, ID " + newVMID + ".");

			// We can create a representation for the new VM, using the returned
			// VM-ID
			VirtualMachine vm = new VirtualMachine(newVMID, oneClient);
			System.out.println(vm.info());
			return vm;
			// Let's hold the VM, so the scheduler won't try to deploy it
			/*System.out.print("Trying to hold the new VM... ");
			rc = vm.hold();

			if (rc.isError()) {
				System.out.println("failed!");
				throw new Exception(rc.getErrorMessage());
			}

			// And now we can request its information.
			rc = vm.info();

			if (rc.isError())
				throw new Exception(rc.getErrorMessage());

			System.out.println();
			System.out
					.println("This is the information OpenNebula stores for the new VM:");
			System.out.println(rc.getMessage() + "\n");

			// This VirtualMachine object has some helpers, so we can access its
			// attributes easily (remember to load the data first using the info
			// method).
			System.out.println("The new VM " + vm.getName() + " has status: "
					+ vm.status());

			// And we can also use xpath expressions
			System.out.println("The path of the disk is");
			System.out.println("\t" + vm.xpath("template/disk/source"));

			// We have also some useful helpers for the actions you can perform
			// on a virtual machine, like cancel or finalize:

			rc = vm.finalizeVM();
			System.out.println("\nTrying to finalize (delete) the VM "
					+ vm.getId() + "...");
	*/
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public static void main(String[] args) {
		System.getProperties().put("http.proxyHost", args[1]);
		System.getProperties().put("http.proxyPort", "3218");
		System.getProperties().put("http.proxyUser", "envisage-vlads");
		System.getProperties().put("http.proxyPassword", "b1neRmTicnh$");
		System.getProperties().put("http.proxySet", "true");
		CloudProvider c= new CloudProvider();
		c.launchInstance(6978, "test");
	}
}
