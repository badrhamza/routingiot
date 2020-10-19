<p>View a network of connected devices and routers with Neo4J. It's also possible to use graph algorithm to select network path that has the lowest latency (case of real-time applications).</p>

![image-20201019173938598](https://github.com/badrhamza/routingiot/blob/master/neo4j.png)

Neo4J is used to store network representation as a graph, on which we can apply graph algorithm like Dijkstra.

![image-20201019174151898](https://github.com/badrhamza/routingiot/blob/master/routing-iot-main-1.png)

The main interface allows you to select your network file (JSON file), so immediately the program trace it to the screen. Your file can contains several thousand nodes, and can hold as much as you need.

![image-20201019174838766](https://github.com/badrhamza/routingiot/blob/master/routing-iot-main-2.png)

After the first view, the "Search" button allows you to select the best path in terms of throughput and latency, from a first node to a second node both already determined.

