package fr.unice.vicc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public class NoViolationPolicy extends VmAllocationPolicy {


    /** The map to track the server that hosts each running VM. */
    private Map<Vm,Host> hoster;	   

    public NoViolationPolicy(List<? extends Host> list) {
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
	   
	 @Override
	 public boolean allocateHostForVm(Vm vm) { 

			int hostlistsize = getHostList().size(); 	    	
	    	boolean isitalloacated = false;
	    	int trynumb = 0;    	
	    	Host actualhost;    	
	    
	    	    	
	    	
	    	do {
	    		actualhost = getHostList().get(trynumb);
	    		if (actualhost.isSuitableForVm(vm)){
	    			if (actualhost.vmCreate(vm) == true) {
	    				  				
						hoster.put(vm, actualhost);
						isitalloacated = true;
						break;
					}
	    		}
	    		
	    		
	    		trynumb++;		
							
			} while((isitalloacated == false) || (trynumb < hostlistsize));
	    	    	
			return isitalloacated; 
	 }
	 
	 boolean decideIfVmInside(List<Vm> vmsOnHost, int trimmedVm){
		 for (Vm vm2 : vmsOnHost) {
			 int vmOnHostIdTrimed = (int) Math.floor(vm2.getId()/100);
			 if (vmOnHostIdTrimed == trimmedVm) {
				return true;					
			}
		 }
		 return false;
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
