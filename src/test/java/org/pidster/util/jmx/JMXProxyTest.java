package org.pidster.util.jmx;

import static org.junit.Assert.*;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JMXProxyTest {

    private String pid;

    private JMXProxy proxy;

    @Before
    public void setUp() throws Exception {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String[] parts = name.split("@");
        this.pid = parts[0];
        this.proxy = JMXProxyFactory.attach(pid);
    }

    @After
    public void tearDown() throws Exception {
        proxy.close();
    }

    @Test
    public void testQueryNamesStringStringString() {
        Set<ObjectName> names = proxy.queryNames("java.lang", "type", "Runtime");
        assertEquals(1, names.size());

        ObjectName name = names.iterator().next();
    }

    @Test
    public void testQueryNamesString() {
        Set<ObjectName> names = proxy.queryNames("java.lang:type=Runtime");
        assertEquals(1, names.size());

        ObjectName name = names.iterator().next();
    }

    @Test
    public void testGetBeanStringClassOfB() {
        RuntimeMXBean bean = proxy.getBean("java.lang:type=Runtime", RuntimeMXBean.class);
        assertNotNull(bean);
        assertTrue(bean.getName().startsWith(pid));
    }

    @Test
    public void testGetBeanStringClassOfBBoolean() {
        RuntimeMXBean bean = proxy.getBean("java.lang:type=Runtime", RuntimeMXBean.class, true);
        assertNotNull(bean);
        assertTrue(bean.getName().startsWith(pid));
    }

    @Test
    public void testGetBeanObjectNameClassOfB() {
        try {
            ObjectName name = new ObjectName("java.lang:type=Runtime");
            RuntimeMXBean bean = proxy.getBean(name, RuntimeMXBean.class);
            assertNotNull(bean);
            assertTrue(bean.getName().startsWith(pid));

        } catch (MalformedObjectNameException e) {
            fail(e.getMessage());
        } catch (NullPointerException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetBeanObjectNameClassOfBBoolean() {
        try {
            ObjectName name = new ObjectName("java.lang:type=Runtime");
            RuntimeMXBean bean = proxy.getBean(name, RuntimeMXBean.class, true);
            assertNotNull(bean);
            assertTrue(bean.getName().startsWith(pid));

        } catch (MalformedObjectNameException e) {
            fail(e.getMessage());
        } catch (NullPointerException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetClassLoadingMXBean() {
        ClassLoadingMXBean bean = proxy.getClassLoadingMXBean();
        assertNotNull(bean);
        assertTrue(bean.getLoadedClassCount() > 10);
    }

    @Test
    public void testGetCompilationMXBean() {
        CompilationMXBean bean = proxy.getCompilationMXBean();
        assertNotNull(bean);
    }

    @Test
    public void testGetGarbageCollectorMXBeans() {
        List<GarbageCollectorMXBean> beans = proxy.getGarbageCollectorMXBeans();
        assertNotNull(beans);

        try {
            for (GarbageCollectorMXBean bean : beans) {
                assertNotNull(bean);
                System.out.printf("GarbageCollector: %s c:%d t:%d %n", bean.getName(), bean.getCollectionCount(), bean.getCollectionTime());
                assertNotNull(bean.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetMemoryPoolMXBeans() {
        List<MemoryPoolMXBean> beans = proxy.getMemoryPoolMXBeans();
        assertNotNull(beans);

        try {
            for (MemoryPoolMXBean bean : beans) {
                assertNotNull(bean);
                System.out.printf("MemoryPool: %s %n", bean.getName());
                assertNotNull(bean.getName());

                // no collections in code cache
                if (!"Code Cache".equals(bean.getName())) {
                    assertNotNull(bean.getCollectionUsage());
                    assertNotNull(bean.getPeakUsage());
                    assertNotNull(bean.getUsage());
                    assertNotNull(bean.getType());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetOperatingSystemMXBean() {
        OperatingSystemMXBean bean = proxy.getOperatingSystemMXBean();
        assertNotNull(bean);
        assertEquals(Runtime.getRuntime().availableProcessors(), bean.getAvailableProcessors());
    }

    @Test
    public void testGetMemoryMXBean() {
        MemoryMXBean bean = proxy.getMemoryMXBean();
        assertNotNull(bean);
        assertNotNull(bean.getHeapMemoryUsage());
        assertNotNull(bean.getNonHeapMemoryUsage());
    }

    @Test
    public void testGetRuntimeMXBean() {
        RuntimeMXBean bean = proxy.getRuntimeMXBean();
        assertNotNull(bean);
    }

    @Test
    public void testGetThreadMXBean() {
        ThreadMXBean bean = proxy.getThreadMXBean();
        assertNotNull(bean);
        assertTrue(bean.getPeakThreadCount() > 0);
        assertTrue(bean.getAllThreadIds().length > 0);
        assertTrue(bean.getThreadCount() > 0);
        assertTrue(bean.getTotalStartedThreadCount() > 0);
    }

}
