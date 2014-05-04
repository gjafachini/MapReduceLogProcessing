Log Processor
======================

Include project description here.

## Architecture
The current project is an extremely simple implementation of a distributed system to process log files. It is strongly based on Map-Reduce primitives, but does not implement all of its properties.

The project is composed of:

 - `processor-master`: Resource and job manager.
 - `processor-node`: Processing node primitives implementation.
 - `processor-ddfs`: Distributed file system implementation.
 - `processor-api`: Public API's
 - `processor-common`: Shared components across modules.
 - `processor-log`: Log processing logic.

## Assumptions
 - Master does not save snapshots of its state and therefore is a single point of failure of the whole system. It can be easily improved by implementing recovery strategies.
 - The whole system was thought to be protocol-agnostic as its components rely on high level interfaces/abstractions. The current implementation uses REST services (HTTP) in order to establish the communication between components, but can be replaced by a more suitable protocol depending the system's architecture and environment.
 - Input partitioning is done at file level. An improved policy could be implemented in order to perform a more efficient sharding.

## Installation:
In order to setup as an Eclipse project and download its dependencies, issue the comands under the project's directory:

    $ mvn eclipse:eclipse

Open Eclipse, select `File > Import...` and Choose `Existing Projects into Workspace`. Browse the project's directory, select all the sub-projects and finish.

## TODO
- Review POM's dependencies.