/*
 *  Copyright 2012 The original authors
 *  
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.pidster.util.jmx;

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;


/**
 * @author <a href="http://pidster.org/">pidster</a>
 *
 */
public class JMXProxy {

    private final JMXConnector connector;

    public JMXProxy(JMXConnector connector) {
        this.connector = connector;
    }

    public void close() {
        Closer.close(connector);
    }

    protected String getConnectionId() {
        try {
            return connector.getConnectionId();
        } catch (IOException e) {
            throw new UnlikelyRuntimeException(e);
        }
    }

    protected MBeanServerConnection getConnection() throws IOException {
        return connector.getMBeanServerConnection();
    }

    protected Set<ObjectName> queryNames(String domain, String key, String value) {
        try {
            ObjectName name = ObjectName.getInstance(domain, key, value);
            return getConnection().queryNames(name, null);
        } catch (MalformedObjectNameException e) {
            throw new QueryException(e);
        } catch (NullPointerException e) {
            throw new QueryException(e);
        } catch (IOException e) {
            throw new QueryException(e);
        }
    }

    protected Set<ObjectName> queryNames(String query) {
        try {
            ObjectName name = new ObjectName(query);
            return getConnection().queryNames(name, null);
        } catch (MalformedObjectNameException e) {
            throw new QueryException(e);
        } catch (NullPointerException e) {
            throw new QueryException(e);
        } catch (IOException e) {
            throw new QueryException(e);
        }
    }

    public <B> B getBean(String name, Class<B> beanClass) {
        return getBean(name, beanClass, false);
    }

    public <B> B getBean(String name, Class<B> beanClass, boolean notificationBroadcaster) {
        try {
            ObjectName on = new ObjectName(name);
            return getBean(on, beanClass, notificationBroadcaster);
        } catch (MalformedObjectNameException e) {
            throw new ProxyException(e);
        }
    }

    public <B> B getBean(ObjectName name, Class<B> beanClass) {
        return getBean(name, beanClass, false);
    }

    public <B> B getBean(ObjectName name, Class<B> beanClass, boolean notificationBroadcaster) {
        try {
            if (JMX.isMXBeanInterface(beanClass)) {
                return JMX.newMBeanProxy(getConnection(), name, beanClass, notificationBroadcaster);
            }
            else {
                return JMX.newMXBeanProxy(getConnection(), name, beanClass, notificationBroadcaster);
            }
        } catch (NullPointerException e) {
            throw new UnlikelyRuntimeException(e);
        } catch (IOException e) {
            throw new ProxyException(e);
        }
    }

    public ClassLoadingMXBean getClassLoadingMXBean() {
        return getBean(ManagementFactory.CLASS_LOADING_MXBEAN_NAME, ClassLoadingMXBean.class, true);
    }

    public CompilationMXBean getCompilationMXBean() {
        return getBean(ManagementFactory.COMPILATION_MXBEAN_NAME, CompilationMXBean.class, true);
    }

    public List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
        
        String query = String.format("%s,name=*", ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE);
        Set<ObjectName> names = queryNames(query);
        
        try {
            List<GarbageCollectorMXBean> beans = new ArrayList<GarbageCollectorMXBean>();

            for (ObjectName on : names) {

                GarbageCollectorMXBeanHandler handler = new GarbageCollectorMXBeanHandler(on, getConnection());

                Class<?>[] arr = new Class[] { GarbageCollectorMXBean.class };
                Object instance = Proxy.newProxyInstance(getClass().getClassLoader(), arr, handler);

                // GarbageCollectorMXBean bean = getBean(on, GarbageCollectorMXBean.class, true);
                beans.add((GarbageCollectorMXBean) instance);
            }
            return beans;

        } catch (Exception e) {
            throw new UnlikelyRuntimeException(e);
        }
    }

    public List<MemoryPoolMXBean> getMemoryPoolMXBeans() {

        String query = String.format("%s,name=*", ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE);
        Set<ObjectName> names = queryNames(query);

        try {
            List<MemoryPoolMXBean> beans = new ArrayList<MemoryPoolMXBean>();

            for (ObjectName on : names) {

                MemoryPoolMXBeanHandler handler = new MemoryPoolMXBeanHandler(on, getConnection());

                Class<?>[] arr = new Class[] { MemoryPoolMXBean.class };
                Object instance = Proxy.newProxyInstance(getClass().getClassLoader(), arr, handler);

                // MemoryPoolMXBean bean = getBean(on, MemoryPoolMXBean.class, true);
                beans.add((MemoryPoolMXBean) instance);
            }
            return beans;

        } catch (Exception e) {
            throw new UnlikelyRuntimeException(e);
        }
    }

    public OperatingSystemMXBean getOperatingSystemMXBean() {
        return getBean(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class, true);
    }

    public MemoryMXBean getMemoryMXBean() {
        try {
            ObjectName name = new ObjectName(ManagementFactory.MEMORY_MXBEAN_NAME);
            MemoryMXBeanHandler handler = new MemoryMXBeanHandler(name, getConnection());
            Class<?>[] arr = new Class[] { MemoryMXBean.class };
            return (MemoryMXBean) Proxy.newProxyInstance(getClass().getClassLoader(), arr, handler);

        } catch (Exception e) {
            throw new UnlikelyRuntimeException(e);
        }
    }

    public RuntimeMXBean getRuntimeMXBean() {
        return getBean(ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class, true);
    }

    public ThreadMXBean getThreadMXBean() {
        return getBean(ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class, true);
    }

}
