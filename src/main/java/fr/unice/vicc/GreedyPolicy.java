


package fr.unice.vicc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public class GreedyPolicy extends VmAllocationPolicy {

	 /** The map to track the server that hosts each running VM. */
    private Map<Vm,Host> hoster;
   
/*
 * Applying the Energy Efficient policy to reduce energy consumption
 * 
 */
    public GreedyPolicy(List<? extends Host> list) {
        super(list);
        hoster =new HashMap<>();
    }

    @Override
    protected void setHostList(List<? extends Host> hostList) {
        super.setHostList(hostList);
        hoster = new HashMap<>();
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> list) {
        return null;
    }
    

  /*
   * Sorting method to get the ID of a currently available for allocation VM with the biggest MIPS and RAM requirements.
   * @param maxres1 = maximum  
   */
    int maxResHostId(int[][] hostAndResource){
    	int maxHostId = 0;
		int maxres1 = hostAndResource[maxHostId][1];
		int maxres2 = hostAndResource[maxHostId][2];
    	
    	for (int i = 1; i < 799; i++) {
			if(hostAndResource[i][1]< maxres1 || hostAndResource[i][2] < maxres2 ){
				maxHostId=i;
				maxres1 = hostAndResource[i][1];
				maxres2 = hostAndResource[i][2];
				
			}
		}    	
    	return maxHostId;
    }   
  
    
    @Override
    public boolean allocateHostForVm(Vm vm) { 
    	boolean isitalloacated = false;
    	
    	int hostnum = 0;
    	int actualMaxId;
    	int[][] hostAndResource = new int[800][3];
    	
    	List<Host> hostlist = getHostList();    	
    	for (Host host : hostlist) {
			hostAndResource[hostnum][0] = host.getId();
			hostAndResource[hostnum][1] = (int) (host.getAvailableMips());
			hostAndResource[hostnum][2] = (int) (host.getRamProvisioner().getAvailableRam());
			hostnum++;					
		}   	    	

    	actualMaxId = maxResHostId(hostAndResource); 		
    	Host actualhost = hostlist.get(actualMaxId);    	

    		do {
    			if (actualhost.isSuitableForVm(vm)){
    			if (actualhost.vmCreate(vm) == true) {
    				hoster.put(vm, actualhost);
    				isitalloacated = true;
     			}
    			
    			}
    			else {
    				hostAndResource[actualMaxId][1]=10000;
    				hostAndResource[actualMaxId][2]=10000;
    				actualMaxId = maxResHostId(hostAndResource);
    				actualhost = hostlist.get(actualMaxId);    				
    			}   

    		} while(isitalloacated == false);

		return isitalloacated;      	   	
    }


    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {    	
    	if (host != null && host.vmCreate(vm)) {
			hoster.put(vm, host);
			return true;
		}
		return false;    	  	
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
    	Host host = this.hoster.remove(vm);    	
    	if (host != null) {
			host.vmDestroy(vm);
		}    	
    }

    @Override
    public Host getHost(Vm vm) {
    	return this.hoster.get(vm);
    	
    }

    @Override
    public Host getHost(int vmId, int userId) {
    	
    	Set<Vm> VMSet = hoster.keySet();
    	for(Vm v : VMSet){
			if(v.getId() == vmId && v.getUserId() == userId){
				return hoster.get(v);
			}
		}   
    	return null; 
    }
}


