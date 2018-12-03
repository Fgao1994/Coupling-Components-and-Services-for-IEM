## Overview

The waterlogging disaster case includes four processes: precipitation, runoff, flow volume and water accumulation. The precipitation, runoff generation and flow concentration are time-dependent processes that will accumulate water on specific area. 


### Waterlogging disaster Case
#### Client:
    cn.edu.whu.openmi.floodmodels.Precipitation.PrecipitationEngine
Precipitation model (OpenMI component). Initialize method is used to read the precipitation in minute; performTimeStep method is to wait scscn model pull the daily precipitation via getValues.

    cn.edu.whu.openmi.floodmodels.Scscn.ScscnEngine
Scscn model (OpenMI component). Initialize method is used to read the CN value; perfromTimeStep method is used to read the daily precipitation from Precipitation model and calculate the runoff, and wait FloodVolumn model to pull.

    cn.edu.whu.openmi.floodmodels.floodvolumn.FloodVolumnEngine
Floodvolumn model (OpenMI component). Initialize method is used to read the area and outlet value of the region.; perfromTimeStep method is used to read the runoff from Scscn model and calculate the water volume, and wait GisVolumn model to pull.

    cn.edu.whu.openmi.floodmodels.http.HttpGisVolumnEngine
The HTTP client of GisVolumn (OpenMI component). Initialize method is used to get a GisVolumn id from the response; performTimeStep method is used to pull the water volume, then send a request with the data to HTTP server.

(note: cn.edu.whu.openmi.util.RequestMethodStore: Establish HTTP connection and send some parameters, the HTTP connection way can be changed to short-alive or long-alive by modify the connection argument “connection”)

    cn.edu.whu.openmi.floodmodels.websocket.WSSyncGisVolumnEngine
The WebSocket client of  GisVolumn (OpenMI component). Initialize method is used to established a WebSocket connection with these kvp; performTimeStep method is used to pull the water volume, then send a request with the data to WebSocket server. 

(note: WebSocket follows asynchronous mechanism, which means the client send request and doesn’t wait the response from server. But the model computation flow requires that the next step computation should wait until the client receives the results. To solve the inconsistency, the thread mechanism is used. The main thread in the client is used to perform time steps, and the other thread is used to send and receive messages)


#### Server:(Tomcat/8.0 and servlet is used)
    cn.edu.whu.openmi.http.HttpGisVolumn
The HTTP Server of GisVolumn. (which is wrapped with OpenMI and Servlet). Initialize method is used to read DEM, performTimeStep is used to get the water volumen, then calculate the elevation of water and return to client. 

(note: For maintaining the state of the model, we used UUID to identify the model and save some initial parameters)

    cn.edu.whu.openmi. websocket2.WSGisVolumn
There are three event handlers that must be supported by the server to implement the WebSocket API: onOpen, onMessage, and onClose. Which match the three phases of OpenMI component. The onOpen method is used to confirm the connecting request from the client. In this method, the initialize phase of the OpenMI model on the server can be invoked, where some time-independent values are set. The onMessage method is used for the time-step based computation. The data exchange during each time step will invoke the onMessage method, which in turn drive the run phase of the OpenMI component and calculate the elevation of water. The onClose method is used to close the connection between the client and the server. It will trigger the finish phase of the OpenMI component.

#### Execution portal 
    cn.edu.whu.openmi.floodmodels.GisVolumn.LocalMainTest
Waterlogging case in local environment.
    
    cn.edu.whu.openmi.floodmodels.http.HttpMainTest
Waterlogging case in HTTP approach.

     cn.edu.whu.openmi.floodmodels.websocket.WSMainTest
Waterlogging case in WebSocket approach.
