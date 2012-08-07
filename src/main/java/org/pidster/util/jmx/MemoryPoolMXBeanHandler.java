package org.pidster.util.jmx;

import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

public class MemoryPoolMXBeanHandler extends AbstractMBeanHandler implements InvocationHandler {

    public MemoryPoolMXBeanHandler(ObjectName name, MBeanServerConnection connection) {
        super(name, connection);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        String attribute = methodName.replaceFirst("get", "");

        if (Void.class.equals(method.getReturnType())) {
            if (methodName.startsWith("set")) {
                return setAttribute(attribute, args);
            }
            else {
                return invoke(attribute, args);
            }
        }

        if (args == null || args.length == 0) {
            Object obj = getAttribute(attribute);
            System.out.println("attribute: " + methodName + " = " + obj);
            if ("getType".equals(methodName)) {
                return MemoryType.valueOf((String) obj);
            }
            else if (obj instanceof CompositeData) {
                CompositeData data = (CompositeData) obj;
                return MemoryUsage.from(data);
            }
            else {
                return obj;
            }
        }

        throw new ProxyAttributeException("Proxy method " + methodName + " not found");
    }

}
