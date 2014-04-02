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
public class JBenchTest
    extends TestCase
{
    @Autowired
    SomeServiceBean someServiceBean = null; // will be set by spring

    @Test
    public void testJBenchJMethodMonitor()
    {
        displayLine();
        Map<String, JBenchData> benchDataMap = JBenchRunner.executeAll(true);
        assertNotNull("JBenchData map object can not be null", benchDataMap);
        assertEquals("JBenchData map object can not be empty", false, benchDataMap.isEmpty());

        JMethodMonitorService service = new JMethodMonitorService();
        for (int i = 0; i < 10; i++)
        {
            someServiceBean.getUUID();
        }
        assertNotNull("Method monitor stats map can not be null", service.getAllStats());
        assertEquals("Method monitor stats map can not be empty", false, service.getAllStats().isEmpty());
        for (Map.Entry<String, JMethodMonitorStatistics> stats : service.getAllStats().entrySet())
        {
            System.out.println(stats.getValue());
        }
        displayLine();
    }

    private void displayLine()
    {
        System.out.println("\n\n--------------------------------------------------------------------------------\n\n");
    }
}
