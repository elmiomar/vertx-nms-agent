# vertx-nms-agent
NDN management agent using vertx-io toolkit.


### Install and Run

#### Build and Run Agent

```
$ mvn clean install
$ java -jar target java -jar target/nms-agent-1.5.0-SNAPSHOT-fat.jar 
```


#### Angular Client

```
$ cd angular-client
```

##### Install dependencies

```
$ npm install
```

##### Run

```
$ npm start
```

visit: [http://localhost:8000](http://localhost:8000)


### To Do

- Add simulator verticle. This verticle is responsible of mimicking the behavior of an NDN node. It creates different network scenarios and runs them. 
- Add NFD verticle. This verticle is the interaction point with the NDN forwarder. It's responsible of querying and configuring NFD.
- Put logging in seperate verticle. Logs are currently stored in the database and are handled by the database verticle. Moving logging logic to a separate verticle would allow more flexibility and integration with other logging tools in the future.
