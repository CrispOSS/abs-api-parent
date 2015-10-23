package abs.api;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennebula.client.vm.VirtualMachine;

public class DeploymentComponent {
//This class has fields that reflect execution state and resource
  // consumption; they are changed by the underlying runtime system.
  
  public static CloudProvider xmlRpc = new CloudProvider();
  String description;
  Map<String, Float> initConfig;
  VirtualMachine  vm;
  
  Long creationTime = System.currentTimeMillis();
  // Histories are updated by the backend.  totalhistories stay empty in
  // case the DC has an infinite amount of resources for that type.
  List<Float> cpuhistory = new ArrayList<Float>();
  List<Float> cpuhistorytotal = new ArrayList<Float>();
  List<Float> bwhistory = new ArrayList<Float>();
  List<Float> bwhistorytotal = new ArrayList<Float>();
  List<Float> memoryhistory = new ArrayList<Float>();
  List<Float> memoryhistorytotal = new ArrayList<Float>();
  
  // Counters of consumed resources in current time unit.  Modified by the
  // backend where appropriate (CPU, bandwidth self-refresh; memory doesn't.)
  float cpuconsumed = 0;
  float bwconsumed = 0;
  float memoryconsumed = 0;
  // Initialized flag read by the backend to avoid premature execution
  boolean initialized = false;
  // Total amount of resources in current and next time interval.
  
  public DeploymentComponent(String description, float memory, float CPU) {
    // TODO Auto-generated constructor stub
    this.description=description;
    initConfig = new HashMap<String, Float>();
    initConfig.put("Memory", memory);
    initConfig.put("CPU", CPU);
    vm = xmlRpc.launchInstance(0, description);
  }
  
  public float total(String resourceType){
    return initConfig.get(resourceType);
  }
  
  public boolean acquire(){
    this.vm.deploy(1);
    return this.initialized;
    
  }
  
  public boolean release(){
    this.vm.shutdown();
    this.vm.delete();
    return true;
    
  }
}
