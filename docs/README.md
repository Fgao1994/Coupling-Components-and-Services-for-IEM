Overview
The used case in the proposed IEM framework involves several hydrological models: Precipitation Model, Hargreaves model, and TOPMODEL. The Precipitation Model, Hargreaves model are time-dependent model that will generate runoff on an area. The primary model used to generate runoff is the Topography-based hydrological MODEL (TOPMODEL), which is a continuous simulation model and has been applied in various regions throughout the world. The model takes the daily precipitation, daily evapotranspiration, and topographic index as inputs, and outputs daily runoff. In the case, we wrapped TOPMODEL as a Http and WebSocket service separately.