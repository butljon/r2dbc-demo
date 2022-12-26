# r2dbc-demo
Spring R2DBC Prototype

Implements against PLSQL R2DBC connector with _localhost_ available DB service (see _src/main/resources/r2dbc-demo.psql_ for schema requirements).

_Transactions_ can be inserted via REST POST with uniqueness criteria (_period_, _sequence_). These will be accumulated into _aggregates_, the latter having uniqueness criteria (_period_).

Play with:

- Spring Data transactionality, 
- error handling (you can force such by e.g. additionally annotating _@Column("a_count")_ with _@NonNull_),
- synchronisation and thread safety,

and see how reactive R2DBC operations behave under concurrency.

_src/main/resources/application-secrets.yml_ will need to be created locally, containing:

_&nbsp;&nbsp;&nbsp;spring:<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;r2dbc:<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;username:_ user<br />
_&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password:_ pass<br />

EPs exposed:

- GET http://localhost:8080/r2dbc-demo/aggregates,
- GET http://localhost:8080/r2dbc-demo/aggregates/{period},
- POST http://localhost:8080/r2dbc-demo/transactions/enter,

latter accepting (JSON) payload such as,

&nbsp;&nbsp;&nbsp;{ "period": 0, "sequence": 0 }

Have fun!

