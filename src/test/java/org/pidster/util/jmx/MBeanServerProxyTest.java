package org.pidster.util.jmx;

import static org.junit.Assert.*;

import java.io.IOException;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Set;

import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MBeanServerProxyTest {

    private String pid;

    private JMXProxy proxy;

    @Before
    public void setup() throws Exception {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String[] parts = name.split("@");
        this.pid = parts[0];

        this.proxy = JMXProxyFactory.attach(pid);
    }

    @Test
    public void testProxy() {

        try {
            MBeanServerConnection connection = proxy.getConnection();
            assertNotNull(connection);

            System.out.println("connection: " + proxy.getConnectionId());

            RuntimeMXBean runtimeMXBean = proxy.getRuntimeMXBean();
            assertNotNull(runtimeMXBean);

            System.out.println("name: " + runtimeMXBean.getName());

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testJSON() {
        try {
            Set<ObjectName> queryNames = proxy.queryNames("java.lang", "type", "Runtime");

            ObjectName objectName = queryNames.iterator().next();
            MBeanInfo info = proxy.getConnection().getMBeanInfo(objectName);
            assertNotNull(objectName);
            assertNotNull(info);

            MBeanInfoJSON json = new MBeanInfoJSON(info);
            System.out.println("name: " + json.toJSON());

            

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @After
    public void teardown() {
        if (proxy != null)
            proxy.close();
    }

}
