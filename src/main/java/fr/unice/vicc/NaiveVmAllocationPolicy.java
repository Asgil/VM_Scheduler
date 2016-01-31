package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.power.PowerHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fhermeni2 on 16/11/2015.
 */
public class NaiveVmAllocationPolicy extends VmAllocationPolicy {

    /** The map to track the server that hosts each running VM. */
    private Map<Vm,Host> hoster;
   

    public NaiveVmAllocationPolicy(List<? extends Host> list) {
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
 public boolean allocateHostForVm(Vm vm) {  
    	
		List<Host> hostlist = getHostList();
		boolean result = false;
		for (Host actualhost : hostlist) {
			if(actualhost.vmCreate(vm)){
				hoster.put(vm, actualhost);
				result = true;
				break;
			}
		}
		return result;		
 } */
   
 @Override
    public boolean allocateHostForVm(Vm vm) {  
    	
		int hostlistsize = getHostList().size(); 	    	
    	boolean isitalloacated = false;
    	int trynumb = 0;    	
    	Host actualhost;    	
    	
    	do {
    		actualhost = getHostList().get(trynumb);
    		
    		if (actualhost.vmCreate(vm) == true) {
				hoster.put(vm, actualhost);
				isitalloacated = true;
				break;
			}    		
    		trynumb++;
			  		
    		//System.out.println("doing  " + actualhost.toString() + "   " + vm.toString());    		   		
						
		} while((isitalloacated == false) || (trynumb < hostlistsize));
    	    	
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
    	//hoster = new HashMap<Vm,Host>();
    	//return this.hoster.get(Vm.getUid(userId, vmId));
    	
    	Set<Vm> VMSet = hoster.keySet();
    	for(Vm v : VMSet){
			if(v.getId() == vmId && v.getUserId() == userId){
				return hoster.get(v);
			}
		}   
    	return null; 
    }
}
