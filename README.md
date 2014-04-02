JBench & JMethodMonitor
=======================
JBench: A small Java annotation driven benchmark testing service framework.
JMethodMonitor: A small Java annotation driven class / method duration monitor service framework.
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
</pre>


JMethodMonitor usage
--------------------
JMethodMonitor can be used to find slow, legacy methods within Java application, or to see the
result of data caching or "lack thereof".

<pre>
 public static void main(String [] args) throws InterruptedException
 {
     final JMethodMonitorDemo demo = new JMethodMonitorDemo();
     for (int i = 0; i < 10; i++)
     {
        // This is a call to a method annotated with @JMethodMonitor
         demo.getUUID();
     }
     for (Map.Entry<String, JMethodMonitorStatistics> stats : service.getAllStats().entrySet())
     {
         System.out.println(stats.getValue());
     }
 }
</pre>

