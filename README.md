# Vicc project: homemade VM Schedulers

This project aims at developing different Vm schedulers for a given IaaS cloud. Each scheduler will have meaningful properties for either the cloud customers or the cloud provider.

## Exercices

###Sheduler I. Naive.

The simulator terminated successfully, all the machines were allocated according to the algorithm allocating the VM to the first host with enough MIPS and RAM.

The summarized output for all days:
```sh
Incomes:    12399.24
Penalties:  402.18
Energy:     2643.81
Revenue:    9353.25
```

We see that we have revenue gained, as well as the SLA violations.


###Sheduler II. AntiAffinity.

Class AntiAffinityVmAllocationPolicy.java implements the VM allocation with regard to their affinity. 
1. The theoretical complexity of the algorithm: Let us assume the machines arrive according to their ID (we have seen it in logs).
Then worst case scenario of complexity is when we schedule the VM to the last Host in the list, the next VM to the last-1. So `(n+n-1+n-2... n-h)*(k/h).toInt` where `n` is the number of VMs, `h` is the # of VM should not be on the same host and `k` is the number of hosts. We take the whole pa of `(k/h)` to get the number of iterations for each VM cluster. 
2. This kind of an algorithm has a negative impact on the cluster capacity, because it introduces additional constraints to the VM allocation policy. It is required to have at least 100 host available for 100 VMs and the host with the minimum capacity should be at least as powerful as the sum of the minimum resources required vm from each set of 100. The resources might be underused and the energy consumption increases  little, because we cannot pack VMs most efficiently.


The summarized output for all days:
```sh
Incomes:    12399.24
Penalties:  187.48
Energy:     2686.90
Revenue:    9524.86
```
	
###Shedulers III. Next-fit. Worst-fit.

The class NextFitPolicy.java implements the provided algorithm for the next fit policy. We save the ID of the last host to which the VM was allocated, and we start searching for the suitable allocation for the next VM from the host ID+1. When we arrive to the last host 799, if we are not able to allocate the VM here - we start again from the host 0. 

The summarized output for all days for `Next-fit`:
```sh
Incomes:    12399.24
Penalties:  182.77
Energy:     3289.35
Revenue:    8927.12

```
Indeed, we observe less penalties with regards to the Naive scheduler.

The class WorstFitPolicy.java implements the worst-fit algorithm. 
We introduce the sorting method to get the ID of a currently available for allocation host with the biggest MIPS or RAM provision `maxResHostId`.
During the implementation we tried to check for the host with both biggest MIPS and RAM. But the impact on the SLA violations were worse.

The summarized output for all days for `Worst-fit`:
```sh
Incomes:    12399.24
Penalties:  6.06
Energy:     3271.79
Revenue:    9121.40
```

1. The best performance in terms of SLA violations reduction is when running Worst-fit algorithm, because machines are allocated to the host with the most remaining resources, so the possibility to have SLA violations due to resoure unavailability is less than in case of the Next-fit.
1. The theoretical complexity: Next-fit: Let us suoppose the next suitable VM is just behind the already allocated one. So it has to check k machines until it finds the next one. So the complexity is `k*n`.
Worst fit: `(k-1)*n`. First we sort the hosts (`k-1` times comparison) and do it for the n VMs.

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
The class EnergyEfficientPolicy.java implements the policy to reduce energy consumption of the data center. We sort hosts by MIPS or RAM in ascending order, so that first we will try to allocate VMs to those hosts, which have least remaining resources. This allow us to pack VMs more efficiently. The complexity is similar to the worst fit, but we sort machines in the descending order.


The summarized output for all days:
```sh
Incomes:    12399.24
Penalties:  1425.26
Energy:     2609.37
Revenue:    8364.61

```

Indeed, it consumes less energy than all other schedulers.

###Sheduler VI. Greedy.

In the Revenue.java class we see that the final revenue depends very much on the consumed energy cost and the penalties introduced. We decided to combine the approaches we used in the schedulers IV and V. We will try to pack VMs as efficient as possible, provided we have no SLA violations.

The summarized output for all days:
```sh
Incomes:    12399.24
Penalties:  0.00
Energy:     2857.52
Revenue:    9541.72
```

The final revenue is bigger than all the revenues obtained before, although, for some of the schedulers - not significantly bigger. We think it is a possible tradeoff between energy consumption and SLA preservation.
The complexity of the algorithm is combined energy efficiency algorithm complexite and the no SLA violations. But since we don't know the complexity of the `isSuitableFor` method we cannot completely infer the overall complexity of the Greedy scheduler.
