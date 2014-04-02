JBench & JMethodMonitor
=======================
JBench: A small Java annotation driven benchmark testing service framework.
JMethodMonitor: A small Java annotation defined, aspect driven, method execution / duration monitor service framework.
Two utility frameworks are packaged together in a single jar using maven.

Running the tests
-----------------
This project is using maven to compile, package and run JUnit test of time series.

    git clone https://github.com/temesoft/jbench.git
    cd jbench
    mvn clean package
    cp target/jbench-ver.jar /your/project/lib/

JBench usage
------------
JBench can be used standalone or as JUnit test to benchmark specific Java class methods.
JBench instantiates new class instances to benchmark using spring framework application
context when available (spring app) or using Java reflection api.

<pre>
 public static void main(String [] args) throws InterruptedException
 {
     JMethodMonitorService service = new JMethodMonitorService();
     JBenchRunner.executeAll(true);
     for (Map.Entry<String, JMethodMonitorStatistics> stats : service.getAllStats().entrySet())
     {
         System.out.println(stats.getValue());
     }
 }

 @JBench
 public class BenchMe
 {
    @JBench(maxIterations =  10000)
    public final void nextRandomDouble()
    {
        double d = rnd.nextDouble();
    }
 }


 .....
 sample output
 .....
 > JMethodMonitorService - started
 > SpringFramework application context is not available
 > Loaded 2 classes containing benchmarks:
 > 	- class: JBench_InternalBenchmarks
 > 	- class: JBenchTest
 > Benchmark name                               Time passed (ns)     Time passed (ms)           Iterations      Speed (exec/ns)      Speed (exec/ms)         Average (ns)
 > ---------------------------------------- -------------------- -------------------- -------------------- -------------------- -------------------- --------------------
 > empty                                             243,761,000                  243          100,000,000          0.411605632              411,606              2.42953
 > ---------------------------------------- -------------------- -------------------- -------------------- -------------------- -------------------- --------------------
 > nextRandomDouble                                   11,547,000                   11               10,000          0.000866176                  866                1,154
 > sqrtOfRandom                                        7,143,000                    7               10,000          0.001400364                1,400                  714
 > collectionsSingletonList                            8,697,000                    8               10,000          0.001150219                1,150                  870
 > newArrayList                                        5,079,000                    5               10,000          0.001969279                1,969                  508
 > newArrayListSynchronized                            6,130,000                    6               10,000          0.001631854                1,632                  613
 > newHashMap                                          5,788,000                    5               10,000          0.001728608                1,729                  579
 > newHashMap_Synchronized                             8,431,000                    8               10,000           0.00118638                1,186                  843
 > uuid                                              127,159,000                  127               10,000          0.000078642                   79               12,716
 > newHashMap_Concurrent                              19,231,000                   19               10,000          0.000520075                  520                1,923


</pre>


JMethodMonitor usage
--------------------
JMethodMonitor can be used to find slow, legacy methods within Java application, or to see the
result of data caching or "lack thereof".

<pre>
 public static void main(String [] args) throws InterruptedException
 {
     final JMethodMonitorDemo test = new JMethodMonitorDemo();
     for (int i = 0; i < 10; i++)
     {
        // This is a call to a method annotated with @JMethodMonitor
         test.getUUID();
     }
     for (Map.Entry<String, JMethodMonitorStatistics> stats : service.getAllStats().entrySet())
     {
         System.out.println(stats.getValue());
     }
 }
</pre>

