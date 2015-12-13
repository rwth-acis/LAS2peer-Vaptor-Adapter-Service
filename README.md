#Vaptor Backend Component

##Adapter Service

It is part of Master Thesis "Adaptive Video Techniques for Informal Learning Support in Workplace Environments" at Chair of Computer Science 5, RWTH-Aachen.

The adapter service implements the FOSP adaptation algorithm to process the search query entered by the user. The front-end queries this service directly, using its REST API. The request is received and search string is parsed. The adapter service then requests the annotation service with the nouns in the search query. The annotation service returns the annotations corresponding to that query. Then the FOSP algorithm is applied and a JSON is formed with details of the relevant segments. This information is sent as a response to the user. Adapter service uses other microservices under Vaptor for different purposes. User Preference Service is requested before applying the FOSP algorithm, requesting the preferences of the user. Segment analytics service is requested to get the weight, assigned to a particular segment. The adapter service also implicitly takes into account, user's device. It considers whether user is using a mobile device or a desktop computer. In both cases slightly different results are shown to the user. In case of mobile devices, only those video segments which are of smaller duration are shown to the user. Also the total size of the adaptive video is kept within 5 minutes limit for mobile devices. 

Compile using ant, with "ant all". Then run bin/start_network.bat for windows or bin/start_network.sh for linux. 