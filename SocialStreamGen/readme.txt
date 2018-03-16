========================================================================
SocialStreamGen
========================================================================

SocialStreamGen is a single node generator version of SSG(Social Stream Generation). 

/////////////////////////////////////////////////////////////////////////////

## Parameters:
*       parameterPath: the path of parameter file "properties.txt", which records a set of parameters.

## Parameters in "properties.txt":
*	startTime: the start time of generated social stream
*	endTime: the end time of generated social stream
*	userNum: the number of user
*	path: the path of the project
*       dataType: the type name of generated social stream. We provide three kinds of dataType in the examples, including "weibo", "patent" and "email". And the corresponding file parameters are provided according to the real-lift data.The file parameters will introduce in the following.
*	windowSize: the time length of caching the timeline in windows size. We provide three windowSize values for three kinds of data, weibo:6580800, patent:81345600 and email:8380800.
*       viewSize: the interval of generated social items outputted in the console. We provide three kinds of viewSize, including "day", "month" and "year".


## File Parameters:
*	linkIntervals: the probability distribution of link interval 
*	producerProporty: the probability distribution of producer post rate
        hour: the hour justment factors
	day: the day justment factors
	week: the week justment factors
	itemOutdegree: the probability distribution of social item. Multi-link social stream should provide "itemOutdegree".
	[dataType]_[userNum]: the producer network corresponding the [dataType] and the [userNum], which could generate using CL model, and the implementation is provided. The detail show in the file of "GenProducerNetwork" stored in "SocialStreamGen/".


File Parameters stored in "SocialStreamGen/[dataType]/data/"
	
## OutFile:

The outFile stored in "SocialStreamGen/out/[dataType]_[userNum]/"