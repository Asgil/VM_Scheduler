# Vicc project: homemade VM Schedulers

This project aims at developing different Vm schedulers for a given IaaS cloud. Each scheduler will have meaningful properties for either the cloud customers or the cloud provider.


## Exercices

###Sheduler I. Naive.

The simulator terminated successfully, all the machines were allocated according to the algorithm allocating the VM to the first host with enough MIPS and RAM.

The summarized output for all days:
```sh
hop
Incomes:    12399.24
Penalties:  402.18
Energy:     2643.81
Revenue:    9353.25
```

We see that we have revenue gained, as well as the SLA violations.


###Sheduler II. AntiAffinity.

Class AntiAffinityVmAllocationPolicy.java implements the VM allocation with regard to their affinity. 
1. The theoretical complexity of the algorithm:
1. This kind of an algorithm has a negative impact on the cluster capacity, because it introduces additional constraints to the VM allocation policy. It is required to have at least 100 host available for 100 VMs. The resources might be underused and the energy consumption increases  little, because we cannot pack VMs most efficiently.

The summarized output for all days:
```sh
hop
Incomes:    12399.24
Penalties:  187.48
Energy:     2686.90
Revenue:    9524.86
```
	
###Shedulers III. Next-fit. Worst-fit.

The class NextFitPolicy.java implements the provided algorithm for the next fit policy. We save the ID of the last host to which the VM was allocated, and we start searching for the suitable allocation for the next VM from the host ID+1. When we arrive to the last host 799, if we are not able to allocate the VM here - we start again from the host 0. 

The summarized output for all days for `Next-fit`:
```sh
hop
hop
Incomes:    12399.24â‚¬
Penalties:  182.77â‚¬
Energy:     3289.35â‚¬
Revenue:    8927.12â‚¬

```
Indeed, we observe less penalties with regards to the Naive scheduler.

The class WorstFitPolicy.java implements the worst-fit algorithm. 
We introduce the sorting method to get the ID of a currently available for allocation host with the biggest MIPS or RAM provision `maxResHostId`.
During the implementation we tried to check for the host with both biggest MIPS and RAM. But the impact on the SLA violations were worse.

The summarized output for all days for `Worst-fit`:
```sh
hop
Incomes:    12399.24
Penalties:  6.06
Energy:     3271.79
Revenue:    9121.40

```

1. The best performance in terms of SLA violations reduction is when running 
1. The theoretical complexity:

###Sheduler IV. No SLA Violations.

The class NoViolationPolicy.java implements the no violation policy. 
For that we are using CloudSim method `isSuitableForVm`, which returns true is the host is suitable for VM by RAM parameters, if yes, all RAM required will be availabe 100% time, so there will be no violations. 

The summarized output for all days:
```sh
Incomes:    12399.24
Penalties:  0.00
Energy:     2872.22
Revenue:    9527.02

```

Indeed, we have no SLA violations.

###Sheduler V. Energy-efficient.

We reach energy efficiency when we pack VMs to the less possible number of hosts, so that some hosts can stay turned off.
The class EnergyEfficientPolicy.java implements the policy to reduce energy consumption of the data center. We sort hosts by MIPS or RAM in ascending order, so that first we will try to allocate VMs to those hosts, which have least remaining resources. This allow us to pack VMs more efficiently. 

The summarized output for all days:
```sh
hop
Incomes:    12399.24â‚¬
Penalties:  1425.26â‚¬
Energy:     2609.37â‚¬
Revenue:    8364.61â‚¬

```

Indeed, it consumes less energy than all other schedulers.

###Sheduler VI. Greedy.

In the Revenue.java class we see that the final revenue depends very much on the consumed energy cost and 

The summarized output for all days:
```sh
hop

```

### Greedy scheduler

Develop a scheduler that maximizes revenues. It is then important to provide a good trade-off between energy savings and penalties for SLA violation. Justify your choices and the theoretical complexity of the algorithm
