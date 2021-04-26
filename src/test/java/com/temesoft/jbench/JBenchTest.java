package com.temesoft.jbench;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Demo executable of @JBench functionality
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context.xml")
public class JBenchTest extends TestCase {
    @Autowired
    SomeServiceBean someServiceBean = null; // will be set by spring

    @Test
    public void testJBench() {
        Map<String, JBenchData> benchDataMap = JBenchRunner.executeAll(true);
        assertNotNull("JBenchData map object can not be null", benchDataMap);
        assertFalse("JBenchData map object can not be empty", benchDataMap.isEmpty());
    }

    @Test
    public void testJMethodMonitor() {
        final JMethodMonitorService service = new JMethodMonitorService();
        for (Map.Entry<String, JMethodMonitorStatistics> stats : service.getAllStats().entrySet()) {
            System.out.println(stats.getValue());
        }
    }
}
