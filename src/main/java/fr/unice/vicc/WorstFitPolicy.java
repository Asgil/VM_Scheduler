package fr.unice.vicc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public class WorstFitPolicy extends VmAllocationPolicy {

	 /** The map to track the server that hosts each running VM. */
    private Map<Vm,Host> hoster;
   

    public WorstFitPolicy(List<? extends Host> list) {
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
    

   
    int maxResHostId(int[][] hostAndResource){
    	int maxres = 0;
    	int maxHostId = 0;
    	
    	for (int i = 0; i < 799; i++) {
			if(hostAndResource[i][1]> maxres){
				maxHostId=i;
			}
		}    	
    	return maxHostId;
    }   
  
    
    @Override
    public boolean allocateHostForVm(Vm vm) { 
    	boolean isitalloacated = false;
    	
    	int hostnum = 0;
    	int actualMaxId;
    	int[][] hostAndResource = new int[800][2];
    	
    	List<Host> hostlist = getHostList();    	
    	for (Host host : hostlist) {
			hostAndResource[hostnum][0] = host.getId();
			hostAndResource[hostnum][1] = (int) (host.getAvailableMips() + host.getRamProvisioner().getAvailableRam());
			hostnum++;					
		}   	    	
    /*	int currentMaxMips = 0;
    	int maxmips;
    	Host hostWithMaxMips;
    	for (Host host : hostlist) {
    		maxmips = (int) host.getAvailableMips();
			if (maxmips>currentMaxMips) {
				currentMaxMips = maxmips;
				hostWithMaxMips=host;			
			}			
		}*/
    	
    	   	
    	actualMaxId = maxResHostId(hostAndResource); 		
    	Host actualhost = hostlist.get(actualMaxId);    	
    	//int trynumb =0;
    	
    	/*if (actualhost.vmCreate(vm) == true) {
			hoster.put(vm, actualhost);
			isitalloacated = true;
			hostAndResource[actualMaxId][1] -= (int) (vm.getRam()+vm.getMips());			
		}
    	else {*/
    		do {        		
    			if (actualhost.vmCreate(vm) == true) {
    				hoster.put(vm, actualhost);
    				isitalloacated = true;
     			}
    			else {
    				hostAndResource[actualMaxId][1]=0;
    				actualMaxId = maxResHostId(hostAndResource);
    				actualhost = hostlist.get(actualMaxId);    				
    			}   
    			
    			/*actualhost = getHostList().get(trynumb);
        		
        		if (actualhost.vmCreate(vm) == true) {
    				hoster.put(vm, actualhost);
    				isitalloacated = true;
    				break;
    			}    		
        		trynumb++; */
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
