# gluttonapp
Example graph application using Apache TinkerPop Gremlin

## Compile

compile application
```bash
mvn clean compile
```

compile and run application
```bash
mvn -q clean compile exec:java -Dexec.mainClass="com.gluttonapp.App"
```

## Sample Data

The `src/resources` folder contains three scripts intended to be used with a Gremlin Console / Gremlin Server setup: 

 - `no-data.groovy`: connects to local Gremlin Server and removes all data in the graph
 - `small-graph.groovy`: connects to local Gremlin Server, purges any existing data and adds a simple graph [vertices: 4, edges: 5] 
 - `full-graph.groovy`: connects to local Gremlin Server, purges any existing data and adds the full graph [vertices: 8, edges: 12]
 
With a Gremlin Server running locally, start up a Gremlin Console with the following, replacing the `/path/to/script.groovy` with the actual path and script file name.:
 
 ```bash
bin/gremlin.sh -i /path/to/script.groovy
```
