========================================================================
ParallelSocialStreamGen_Worker
========================================================================


/////////////////////////////////////////////////////////////////////////////

## Compilation

The compilation uses Apache Maven to automatically detect and download the necessary dependencies. See: maven.apache.org.

Make sure you are in your Worker/ project folder.
To generate the jar containing all the dependencies the following maven instruction is used:

mvn assembly:assembly

This can lead to the generation of a jar in the target folder the default one called Worker-0.0.1-SNAPSHOT-jar-with-dependencies.


## Parameters:

*	ip: the ip of the master.
*       port: The port number of the master.
*	workerPath: the project path of Worker
*       viewSize: the interval of generated social items outputted in the console. We provide three kinds of viewSize, including "day", "month" and "year".


## File Parameters:
*	linkIntervals: the probability distribution of link interval 
        hour: the hour justment factors
	day: the day justment factors
	week: the week justment factors
	itemOutdegree: the probability distribution of social item. Multi-link social stream should provide "itemOutdegree".
*	subNetwork_[workerID]:
                uid, sumPRate_workerID, sumPRate_workerID/sumPRate, link0,link1,link2
		…
*	userInformation_[workerID]:
		uid, rpRate, pRate
		…

The file parameters of linkIntervals, hour, day, week, itemOutdegree are stored in "Worker/data/[dataType]/".
The file parameters of subNetwork_[workerID] and userInformation_[workerID] are stored in “Worker/data/[dataType]/[userNum_par{partitionNum}]/“.

##Execution

java -jar worker.jar [ip] [port] [workerPath] [viewSize]
		