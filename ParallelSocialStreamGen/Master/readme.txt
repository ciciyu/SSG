========================================================================
ParallelSocialStreamGen_Master
========================================================================



/////////////////////////////////////////////////////////////////////////////

## Compilation

The compilation uses Apache Maven to automatically detect and download the necessary dependencies. See: maven.apache.org.

Make sure you are in your Master/ project folder.
To generate the jar containing all the dependencies the following maven instruction is used:

mvn assembly:assembly

This can lead to the generation of a jar in the target folder the default one called Master-0.0.1-SNAPSHOT-jar-with-dependencies.


## Parameters:

*	path: the path of parameter file "properties.txt", which records a set of parameters.
*       port: The port number of the master.

## Parameters in "properties.txt":
*	startTime: the start time of generated social stream
*	endTime: the end time of generated social stream
*	userNum: the number of user
*	workerNum: the number of workers
*	workerPath: the project path of Worker
*	masterPath: the project path of Worker
*       dataType: the type name of generated social stream. We provide three kinds of dataType in the examples, including "weibo", "patent" and "email". And the corresponding file parameters are provided according to the real-lift data.The file parameters will introduce in the following.


## File Parameters:
*       socialNetworkInSlaves:
             uid, (workerID1_friendsLiveIn, pro_workerID1), (workerID2_friendsLiveIn, spro_workerID2),…

File Parameters stored in "/data/[dataType]/[userNum]_par[partitionNum]/"

##Execution

java -jar master.jar [path] [port]

## OutFile:

The outFile stored in "SocialStreamGen/out/[dataType]_[userNum]/"
		
