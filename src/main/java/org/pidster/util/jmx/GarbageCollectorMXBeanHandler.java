package org.pidster.util.jmx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;


public class GarbageCollectorMXBeanHandler extends AbstractMBeanHandler implements InvocationHandler {

    public GarbageCollectorMXBeanHandler(ObjectName name, MBeanServerConnection connection) {
        super(name, connection);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        String methodName = method.getName();
        String attribute = methodName.replaceFirst("get", "");

        if (methodName.startsWith("get")) {
            return getAttribute(attribute);
        }
        else if (Void.class.equals(method.getReturnType())) {
            if (methodName.startsWith("set")) {
                return setAttribute(attribute, args);
            }
            else {
                return invoke(attribute, args);
            }
        }

        throw new ProxyAttributeException("Proxy method " + methodName + " not found");
    }

}
