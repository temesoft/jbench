package com.temesoft.jbench;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * <p>
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
public final class JBenchRunner implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(JBenchRunner.class);

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
        return execute(displayOutput, annotated.toArray(new Class[0]));
    }

    public static Map<String, JBenchData> execute(final boolean displayOutput, final Class... classesArray) {
        final Map<String, JBenchData> benchmarkData = new HashMap<String, JBenchData>();
        if (applicationContext == null) {
            System.out.println("SpringFramework application context is not available");
        } else {
            System.out.println("SpringFramework application context is available");
        }
        if (classesArray != null) {
            final List<Class> classes = new ArrayList<>();
            classes.add(JBench_InternalBenchmarks.class);
            classes.addAll(Arrays.asList(classesArray));
            System.out.println(String.format("Loaded %s classes containing benchmarks:", classes.size()));
            // Printing out benchmark class / bean details
            for (final Class aClass : classes) {
                final String camelCasedBeanName = camelCaseWord(aClass.getSimpleName());
                Object possibleInstance = null;
                try {
                    possibleInstance = applicationContext.getBean(camelCasedBeanName);
                    System.out.println(String.format("\t- bean: %s", aClass.getSimpleName()));
                } catch (Exception e) {
                    // not a spring bean
                }
                // If the spring bean of this class is not found try creating instance using newInstance()
                if (possibleInstance == null) {
                    System.out.println(String.format("\t- class: %s", aClass.getSimpleName()));
                }
            }
            System.out.println("\t\t* spring context to instantiate");

            printDefaultHeader(displayOutput);

            int benchCounter = 0;
            for (final Class clazz : classes) {
                try {
                    final String camelCasedBeanName = camelCaseWord(clazz.getSimpleName());
                    Object possibleInstance = null;
                    boolean isSpringBean = false;
                    try {
                        possibleInstance = applicationContext.getBean(camelCasedBeanName);
                        isSpringBean = true;
                    } catch (Exception e) {
                        // not a spring bean
                    }
                    // If the spring bean of this class is not found try creating instance using newInstance()
                    if (possibleInstance == null) {
                        possibleInstance = clazz.newInstance();
                    }

                    final Object newInstance = possibleInstance;
                    final Method[] methods = clazz.getMethods();
                    for (final Method method : methods) {
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
                            try {
                                do {
                                    method.invoke(newInstance, null);
                                    iterations++;
                                }
                                while (iterations < precisionOfIteration);
                            } catch (Exception e) {
                                LOGGER.error("Error during execution of method: {}", method, e);
                            }

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
                } catch (Exception e) {
                    LOGGER.error("Error during JBenchRunner execution", e);
                }
            }
        }

        // create Velocity template output into temp location
        if (!benchmarkData.isEmpty()) {
            try {
                final List<String> keys = new ArrayList<String>(benchmarkData.keySet());

                final File temp = new File(String.format("%s/jbench-%s.html", System.getProperty("java.io.tmpdir"), System.currentTimeMillis()));
                System.out.println("Benchmarks HTML output: " + temp.getAbsolutePath());
                final Properties props = new Properties();
                props.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
                props.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
                final VelocityEngine ve = new VelocityEngine(props);
                ve.init();
                final Template t = ve.getTemplate("benchmark-output.vm");
                final VelocityContext context = new VelocityContext();
                final StringBuilder tableBody = new StringBuilder();
                Collections.sort(keys);
                for (final String benchmarkKey : keys) {
                    final JBenchData data = benchmarkData.get(benchmarkKey);
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
                try (final FileOutputStream out = new FileOutputStream(temp)) {
                    context.put("tableBody", tableBody.toString());
                    final StringWriter writer = new StringWriter();
                    t.merge(context, writer);
                    out.write(writer.toString().getBytes());
                }
            } catch (IOException e) {
                LOGGER.error("Error during JBenchRunner execution", e);
            }
        }

        return benchmarkData;
    }

    private static void outputBenchmark(final Map<String, JBenchData> benchmarkData,
                                        final long iterations,
                                        final long startTime,
                                        final Method method,
                                        final boolean displayOutput,
                                        final boolean isSpringBean) {
        final double speedItrPerNs = (double) iterations / (double) (getCurrentTime() - startTime);
        final double speedNsPerItr = (double) (getCurrentTime() - startTime) / (double) iterations;
        final double speedItrPerMs = speedItrPerNs * 1000000;
        final String name = (isSpringBean ?
                String.format("%s.%s *", method.getDeclaringClass().getSimpleName(), method.getName()) :
                String.format("%s.%s", method.getDeclaringClass().getSimpleName(), method.getName()));
        final JBenchData data = new JBenchData(
                name,
                (getCurrentTime() - startTime),
                ((double) (getCurrentTime() - startTime)) / 1000000d,
                iterations,
                speedItrPerNs,
                speedItrPerMs,
                (speedNsPerItr),
                (speedNsPerItr / 1000000d));
        if (displayOutput) {
            final String output = String.format(defaultOutputPattern,
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
            final String output = String.format(defaultHeaderPattern,
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
        final String output2 = String.format(defaultHeaderPattern,
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

    private static String formatNumber(final double number) {
        if (number > 10) {
            nFmt.setMaximumFractionDigits(0);
        } else {
            nFmt.setMaximumFractionDigits(4);
        }
        return nFmt.format(number);
    }

    private static String camelCaseWord(final String src) {
        final StringBuilder camelCasedBeanName = new StringBuilder(src);
        camelCasedBeanName.setCharAt(0, Character.toLowerCase(camelCasedBeanName.charAt(0)));
        return camelCasedBeanName.toString();
    }

    @Autowired
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        applicationContext = context;
    }
}
