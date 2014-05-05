Log Processor
======================

Include project description here.

## Architecture
The current project is an extremely simple implementation of a local system to process log files.
It is strongly based on Map-Reduce primitives, but does not implement all of its properties.

The project is composed of 3 main packages simulating a Map-reduce distributed architecture:
- dfs
The DFS package simulate a distributed file system and provide to the users an transparent interface for interacting with files and folders.

- master
The master package contains the classes that deals with the nodes services and jobs submissions,
orchestrating jobs to idle nodes and its steps.
MasterService provides an simple interface for starting the Map-Reduce engine.

- node
The node package provides the interface for the implementation of the user Map-Reduce process,
it comes with the basic map and reduce methods, called by a master service and some auxiliaries
isIdle and shuffle.


## Assumptions
 - I assumed that the system is data bounded, i.e. any single log file can fit in memory,
 so there is no splitting process implemented.
 - The process starts from a environment class, where the DFS, Master and Node services are
 configured and the jobs submitted.
 - Disk space is not a problem, during the process some temporary files are created for buffering
 propose, as woudn't be possible to load all data in memory.

## Installation:
In order to setup as an Eclipse project and download its dependencies, issue the comands under the project's directory:

    $ mvn eclipse:eclipse

Open Eclipse, select `File > Import...` and Choose `Existing Projects into Workspace`.
Browse the project's directory, select all the sub-projects and finish.

Setup your environment as the example in StartEnv.java and run your process.

