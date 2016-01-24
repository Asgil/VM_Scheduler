# Vicc project: homemade VM Schedulers

This project aims at developing different Vm schedulers for a given IaaS cloud. Each scheduler will have meaningful properties for either the cloud customers or the cloud provider.

The implementation and the evaluation will be made over the IaaS cloud simulator [CloudSim](http://www.cloudbus.org/cloudsim/). The simulator will replay a workload extracted from Emulab, on a datacenter having realistic characteristics. 

Some usefull resources:

- CloudSim [FAQ](https://code.google.com/p/cloudsim/wiki/FAQ#Policies_and_algorithms)
- CloudSim [API](http://www.cloudbus.org/cloudsim/doc/api/index.html)
- CloudSim [source code](cloudsim-3.0.3-src.tar.gz)
- CloudSim [mailing-list](https://groups.google.com/forum/#!forum/cloudsim)
- 
## Setting up the environment

You must have a working Java 7 + [maven](http://maven.apache.org) environment to develop and Git to manage the sources. No IDE is necessary but feel free to use it.

1. clone this repository. The project directory is organized as follow:
```sh
$ tree
 |- src #the source code
 |- repository #external dependencies
 |- planetlab #the workload to process
 |-cloudsim-3.0.3-src.tar.gz # simulator sources
 \- pom.xml # maven project descriptor
```
2. check everything is working by typing `mvn install` in the root directory
3. Integrate the project with your IDE if needed

## How to test

`fr.unice.vicc.Main` is the entry point. It can be launch from your IDE or using the command `mvn compile exec:java`.

```sh
Usage: Main scheduler [day]
```

- `scheduler` is the identifier of the scheduler to test, prefixed by `--`.
- `day` is optional, it is one of the workload day (see folders in `planetlab`). When `all` is indicated all the days are replayed sequentially.

By default, the output is written in a log file in the `logs` folder.

If you execute the program through `mvn exec:java`, then the arguments are provided using the 'sched' and the 'day' properties.

- To execute the simulator using the `naive` scheduler and all the days:
`mvn compile exec:java -Dsched=naive -Dday=all`
- to replay only day `20110303`: `mvn compile exec:java -Dsched=naive -Dday=20110303`

## Exercices

For this project, you have to develop various VM schedulers.
To integrate your schedulers within the codebase, you will have to declare your schedulers inside the class `VmAllocationPolicyFactory`.

### A naive scheduler to start

This first scheduler aims only at discovering the CloudSim API. This scheduler simply places each `Vm` to the first `Host` with enough free capacity.

1. Just create the new class handling the scheduling, integrate it into `VmAllocationPolicyFactory`. Your class must extends `VmAllocationPolicy`. The flag to call this scheduler for the command line interface (CLI) will be "naive". Test if the integration is correct. The code shall crash in your class but that is expected at this stage.
2. Implements the easy part first, that is to indicate where a Vm runs. This is done by the `getHost(Vm)` and the `getHost(int, int)` methods
3. The 2 `allocateHostForVm` are the core of the Vm scheduler. One of the 2 methods will be executed directly by the simulator each time a Vm is submitted. In these methods, you are in charge of compute the most appropriate host for each Vm. Implementing `allocateHostForVm(Vm, Host)` is straighforward as the host is forced. To allocate the Vm on a host look at the method `Host.vmCreate(Vm)`. It allocates and returns true iff the host as sufficient free resources. The method `getHostList` from `VmAllocationPolicy` allows to get the datacenter nodes. Track the way you want the host used to host that Vm.
4. Implements `deallocateHostForVm`, the method that remove a running `Vm` from its hosting node. Find the host that is running your Vm and use `Host.vmDestroy()` to kill it.
5. The scheduler is static. `optimizeAllocation` must returns `null`
6. Now, implement `allocateHostForVm(Vm)` that is the main method of this class. As we said, the scheduler is very simple, it just schedule the `Vm` on the first appropriate `Host`.
7. Test your simulator on a single day. If the simulation terminates successfully, all the VMs have been scheduled, all the cloudlets ran, and the provider revenues is displayed.
8. Test the simulator runs successfully on all the days. For future comparisons, save the daily revenues and the global one. At this stage, it is ok to have penalties due to SLA violations
	
### Support for Highly-Available applications

Let consider the VMs run replicated applications. To make them fault-tolerant to hardware failure, the customer expects to have the replicas running on distinct hosts.

1. Implement a new scheduler (`antiAffinity` flag) that place the Vms with regards to their affinity. In practice, all Vms with an id between [0-99] must be on distinct nodes, the same with Vms having an id between [100-199], [200-299], ... .
1. What is the theoretical complexity of this algorithm ?
1. What is the impact of such an algorithm over the cluster hosting capacity ? Why ?

### Balance the load

1. Develop a scheduler that performs load balancing using a (`next fit algorithm`[http://lmgtfy.com/?q=next+fit+algorithm] (flag `nextFit`). You should observe fewer penalties with regards to the naive scheduler.
1. Develop another algorithm based on a `worst fit algorithm` (`worstFit` flag) that balance with regards to both RAM and mips. Justify  the method you choosed to consider the two dimensions (it is ok to compare against internal tries).
1. Which algorithms performs the best in terms of reducing the SLA violation. Why ?
1. What is the theoretical complexity of each of the implemented scheduler ?

### Get rid of SLA violations

For a practical understanding of what a SLA violation is in this project, look at the `Revenue` class. Basically, there is a SLA violation when the associated Vm is requiring more MIPS it is possible to get on its host.
If the SLA is not met then the provider must pay penalties to the client. It is then not desirable to have violations to attract customers and maximize the revenues.

1. Implement a scheduler that ensures there can be no SLA violation (`noViolations` flag). Remember the nature of the hypervisor in terms of CPU allocation and the VM templates. Your scheduler is effective when you can successfully simulate all the days, with the `Revenue` class reporting no re-fundings due to SLA violation.

### Energy-efficient schedulers

Develop a scheduler (`energy` flag) that reduces the overall energy consumption without relying on Vm migration. The resulting simulation must consumes less energy than all the previous schedulers. Justify your choices

### Greedy scheduler

Develop a scheduler that maximizes revenues. It is then important to provide a good trade-off between energy savings and penalties for SLA violation. Justify your choices and the theoretical complexity of the algorithm
