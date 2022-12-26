# r2dbc-demo
Spring R2DBC Prototype

Implements against PLSQL R2DBC connector with e.g. `localhost` available DB service (see `src/main/resources/r2dbc-demo.psql` for schema requirements).

_Transactions_ can be inserted via REST POST with uniqueness criteria `(period, sequence)`. These will be accumulated into _aggregates_, the latter having uniqueness criteria `(period)`.

Play with:

- Spring Data transactionality, 
- error handling (you can force such by e.g. additionally annotating `@Column("a_count")` with `@NonNull`),
- synchronisation and thread safety,

and see how reactive R2DBC operations behave under concurrency.

`src/main/resources/application-secrets.yml` will need to be created locally, containing:

&nbsp;&nbsp;&nbsp;`spring:`<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`r2dbc:`<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`username:` user<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`password:` pass<br />

(make sure not to forget _secrets_ as a Spring profile, e.g. `mvn spring-boot:run -Dspring-boot.run.profiles=secrets`)

EPs exposed:

- GET http://localhost:8080/r2dbc-demo/aggregates,
- GET http://localhost:8080/r2dbc-demo/aggregates/{period},
- POST http://localhost:8080/r2dbc-demo/transactions/post,

latter accepting (JSON) payload such as,

&nbsp;&nbsp;&nbsp;`{ "period": 0, "sequence": 0 }`

Have a look at `src/main/resources/jmeter/r2dbc-demo_POST_transaction.jmx` for a sample Apache JMeter config. which can be run, e.g. `env JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 jmeter -n -t r2dbc-demo_POST_transaction.jmx -l results.csv` (`env JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 jmeter` in UI).

Have fun!

