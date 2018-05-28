Problem
-
Design and implement a RESTful API (including data model and the backing implementation) for money transfers between accounts.

Explicit requirements:

1 - keep it simple and to the point (e.g. no need to implement any authentication, assume the APi is
invoked by another internal system/service)  
2 - use whatever frameworks/libraries you like (except Spring, sorry!) but don't forget about the requirement #1  
3 - the datastore should run in-memory for the sake of this test  
4 - the final result should be executable as a standalone program (should not require a pre-installed container/server)  
5 - demonstrate with tests that the API works as expected

Implicit requirements:  
1 - the code produced by you is expected to be of high quality.  
2 - there are no detailed requirements, use common sense.

Solution
-
For this problem I have chosen blockchain like architecture where only immutable database objects are used. Each transaction contains amount of transfer, balance, link to the previous transaction and hash, computed from all fields of this transaction and previous transaction. Double spend is prevented by unique index in DB for the field “prevTransactionId”.  
This architecture is great because it is reliable, provides immutable transaction log, uses only one table, very friendly to PostgreSQL architecture (PostgreSQL likes inserts only architecture) and can be very easy scalable (for example through DB partitioning). Signing of each transaction provide ability to detect intrusion in transactions' integrity. In case of DB accidents such database can be easily repaired.

In addition, this solution required very small dependencies. 
 
Look API in [ru.eamosov.revolut.api.BankService](src/main/java/ru/eamosov/revolut/api/BankService.java) or [docs/index.html](https://eamosov.github.io/revolut/)

Building and testing:   
mvn clean install

Running from command line:  
mvn exec:java -D 'PG_URI=jdbc:postgresql://localhost/revolut?user=revolut'  
Empty database and user must be created before running.

or with in-memory database:  
mvn exec:java  

The program starts HTTP server at port 8080 and provides REST interface:  
/create?id=${id}&amp;balance=${balance}  
/history?id=${id}  
/balance?id=${id}  
/transfer?src=${src}&amp;dst=${dst}&amp;amount=${amount}  


