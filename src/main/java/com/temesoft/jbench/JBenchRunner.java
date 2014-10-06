package com.temesoft.jbench;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Single thread static runner, executor of the benchmark test annotated methods.
 *
 * Example call:
 *
 * <pre>
 *  JBenchRunner.execute(true, BenchTest.class);
 *
 *  public class BenchTest
 *  {
 *      @JBench(maxIterations = 100)
 *      public void foo()
 *      {
 *          // do something here...
 *      }
 *  }
 * </pre>
 */
@Component
public final class JBenchRunner
        implements ApplicationContextAware {

    @Autowired
    private static ApplicationContext applicationContext = null;

    static final NumberFormat nFmt = NumberFormat.getNumberInstance();

    static {
        nFmt.setGroupingUsed(true);
        nFmt.setMinimumFractionDigits(0);
    }

    private final static String defaultHeaderPattern = "%-50s %20s %20s %20s %20s %20s";
    protected final static String defaultOutputPattern = "%20s %20s %20s %20s %20s";

    public static Map<String, JBenchData> executeAll(final boolean displayOutput) {
        Reflections reflections = new Reflections(""); // get all classes
        List<Class<?>> annotated = new ArrayList<Class<?>>(reflections.getTypesAnnotatedWith(JBench.class));
        return execute(displayOutput, annotated.toArray(new Class[annotated.size()]));
    }

    public static Map<String, JBenchData> execute(final boolean displayOutput, Class... classesArray) {
        final Map<String, JBenchData> benchmarkData = new HashMap<String, JBenchData>();
        if (applicationContext == null) {
            System.out.println("SpringFramework application context is not available");
        } else {
            System.out.println("SpringFramework application context is available");
        }
        if (classesArray != null) {
            List<Class> classes = new ArrayList<Class>();
            classes.add(JBench_InternalBenchmarks.class);
            classes.addAll(Arrays.asList(classesArray));
            System.out.println(String.format("Loaded %s classes containing benchmarks:", classes.size()));
            // Printing out benchmark class / bean details
            for (int i = 0; i < classes.size(); i++) {
                Class aClass = classes.get(i);
                final String camelCasedBeanName = camelCaseWord(aClass.getSimpleName());
                Object possibleInstance = null;
                try {
                    possibleInstance = applicationContext.getBean(camelCasedBeanName);
                    if (possibleInstance != null) {
                        System.out.println(String.format("\t- bean: %s", aClass.getSimpleName()));
                    }
                } catch (Exception e) {
                }
                // If the spring bean of this class is not found try creating instance using newInstance()
                if (possibleInstance == null) {
                    System.out.println(String.format("\t- class: %s", aClass.getSimpleName()));
                }
            }
            System.out.println("\t\t* spring context to instantiate");

            printDefaultHeader(displayOutput);

            int benchCounter = 0;
            for (int i = 0; i < classes.size(); i++) {
                try {
                    final Class clazz = classes.get(i);
                    final String camelCasedBeanName = camelCaseWord(clazz.getSimpleName());
                    Object possibleInstance = null;
                    boolean isSpringBean = false;
                    try {
                        possibleInstance = applicationContext.getBean(camelCasedBeanName);
                        if (possibleInstance != null) {
                            isSpringBean = true;
                        }
                    } catch (Exception e) {
                    }
                    // If the spring bean of this class is not found try creating instance using newInstance()
                    if (possibleInstance == null) {
                        possibleInstance = clazz.newInstance();
                    }

                    final Object newInstance = possibleInstance;
                    final Method[] methods = clazz.getMethods();
                    for (int j = 0; j < methods.length; j++) {
                        final Method method = methods[j];
                        method.setAccessible(true);
                        if (method.isAnnotationPresent(JBench.class)) {
                            if (displayOutput) {
                                String name = (isSpringBean ?
                                               String.format("%s.%s *", method.getDeclaringClass().getSimpleName(), method.getName()) :
                                               String.format("%s.%s", method.getDeclaringClass().getSimpleName(), method.getName()));
                                String workingOnOutput = String.format("%-50s ", name);
                                System.out.print(workingOnOutput);
                            }
                            JBench annotation = method.getAnnotation(JBench.class);
                            final long precisionOfIteration = annotation.maxIterations();
                            long iterations = 0;
                            final long startTime = getCurrentTime();
                            do {
                                method.invoke(newInstance, null); // TODO:  <-- This is much faster execution
                                //                                        method.invoke(newInstance); // TODO: than this one.... WHY?
                                iterations++;
                            }
                            while (iterations < precisionOfIteration);

                            outputBenchmark(benchmarkData, iterations, startTime, method, displayOutput, isSpringBean);
                            if (displayOutput) {
                                benchCounter++;
                                if (benchCounter == 2) // display line after 2 internal benchmarks
                                {
                                    printLines();
                                }
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }

        // create Velocity template output into temp location
        if (!benchmarkData.isEmpty()) {
            try {
                List<String> keys = new ArrayList<String>(benchmarkData.keySet());

                File temp = new File(String.format("%s/jbench-%s.html", System.getProperty("java.io.tmpdir"), System.currentTimeMillis()));
                System.out.println(String.format("Benchmarks HTML output: " + temp.getAbsolutePath()));
                Properties props = new Properties();
                props.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
                props.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
                VelocityEngine ve = new VelocityEngine(props);
                ve.init();
                Template t = ve.getTemplate("benchmark-output.vm");
                VelocityContext context = new VelocityContext();
                StringBuilder tableBody = new StringBuilder();
                Collections.sort(keys);
                for (String benchmarkKey : keys) {
                    JBenchData data = benchmarkData.get(benchmarkKey);
                    tableBody
                            .append("<tr>")
                            .append("<td>").append(data.getName()).append("</td>")
                            .append("<td>").append(formatNumber(data.getTimePassedNs())).append("</td>")
                            .append("<td>").append(formatNumber(data.getTimePassedMs())).append("</td>")
                            .append("<td>").append(formatNumber(data.getIterations())).append("</td>")
                            .append("<td>").append(formatNumber(data.getSpeedMs())).append("</td>")
                            .append("<td>").append(formatNumber(data.getAverageMs())).append("</td>")
                            .append("</tr>");
                }

                context.put("tableBody", tableBody.toString());
                StringWriter writer = new StringWriter();
                t.merge(context, writer);
                FileOutputStream out = new FileOutputStream(temp);
                out.write(writer.toString().getBytes());
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return benchmarkData;
    }

    private static void outputBenchmark(final Map<String, JBenchData> benchmarkData, long iterations, long startTime,
                                        Method method, final boolean displayOutput, final boolean isSpringBean) {
        double speedItrPerNs = (double) iterations / (double) (getCurrentTime() - startTime);
        double speedNsPerItr = (double) (getCurrentTime() - startTime) / (double) iterations;
        double speedItrPerMs = speedItrPerNs * 1000000;
        String name = (isSpringBean ?
                       String.format("%s.%s *", method.getDeclaringClass().getSimpleName(), method.getName()) :
                       String.format("%s.%s", method.getDeclaringClass().getSimpleName(), method.getName()));
        JBenchData data = new JBenchData(
                name,
                (getCurrentTime() - startTime),
                (getCurrentTime() - startTime) / 1000000l,
                iterations,
                speedItrPerNs,
                speedItrPerMs,
                (speedNsPerItr),
                (speedNsPerItr / 1000000l));
        if (displayOutput) {
            String output = String.format(defaultOutputPattern,
                                          formatNumber(data.getTimePassedNs()),
                                          formatNumber(data.getTimePassedMs()),
                                          formatNumber(data.getIterations()),
                                          formatNumber(data.getSpeedMs()),
                                          formatNumber(data.getAverageMs()));
            System.out.println(output);
        }
        benchmarkData.put(name, data);
    }

    private static void printDefaultHeader(final boolean displayOutput) {
        if (displayOutput) {
            String output = String.format(defaultHeaderPattern,
                                          "Benchmark name",
                                          "Time passed (ns)",
                                          "Time passed (ms)",
                                          "Iterations",
                                          "Speed (exec/ms)",
                                          "Average (ms)");
            System.out.println(output);
            printLines();
        }
    }

    private static void printLines() {
        String output2 = String.format(defaultHeaderPattern,
                                       "----------------------------------------",
                                       "--------------------",
                                       "--------------------",
                                       "--------------------",
                                       "--------------------",
                                       "--------------------");
        System.out.println(output2);
    }


    private static long getCurrentTime() {
        return System.nanoTime();
    }

    private static String formatNumber(double number) {
        if (number > 10) {
            nFmt.setMaximumFractionDigits(0);
        } else {
            nFmt.setMaximumFractionDigits(4);
        }
        return nFmt.format(number);
    }

    private static String camelCaseWord(String src) {
        StringBuilder camelCasedBeanName = new StringBuilder(src);
        camelCasedBeanName.setCharAt(0, Character.toLowerCase(camelCasedBeanName.charAt(0)));
        return camelCasedBeanName.toString();
    }

    @Autowired
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }
}
