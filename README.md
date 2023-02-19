Ensure you have maven installed.

You first must run the server. To run the server, go to the root directory of this project and run

`mvn exec:java -Dexec.mainClass="Runner.MainServer" -Dexec.args="<Port 1> <Port 2>"`

Next, run the client by staying in the root directory and running

`mvn exec:java -Dexec.mainClass="Runner.MainClient" -Dexec.args="<Port 1> <Port 2> <Server IP>"`

Benchmark results will be output on the client side. 