package org.pidster.util.jmx;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public abstract class AbstractMBeanHandler {

    private ObjectName name;

    private MBeanServerConnection connection;

    public AbstractMBeanHandler(ObjectName name, MBeanServerConnection connection) {
        super();
        this.name = name;
        this.connection = connection;
    }

    protected Object invoke(String attributeName, Object[] params) {
        try {
            String[] signature = null;
            if (params != null) {
                Set<String> s = new HashSet<String>();
                for (Object o : params) {
                    s.add(o.getClass().getName());
                }
                signature = new String[params.length];
                signature = s.toArray(signature);
            }

            return connection.invoke(name, attributeName, params, signature);

        } catch (InstanceNotFoundException e) {
            throw new ProxyAttributeException(e);
        } catch (MBeanException e) {
            throw new ProxyAttributeException(e);
        } catch (ReflectionException e) {
            throw new ProxyAttributeException(e);
        } catch (IOException e) {
            throw new ProxyAttributeException(e);
        }
    }

    protected Void setAttribute(String attributeName, Object[] args) {
        try {
            Attribute attribute = new Attribute(attributeName, args);
            connection.setAttribute(name, attribute);
            return Void.class.newInstance();

        } catch (InstanceNotFoundException e) {
            throw new ProxyAttributeException(e);
        } catch (AttributeNotFoundException e) {
            throw new ProxyAttributeException(e);
        } catch (InvalidAttributeValueException e) {
            throw new ProxyAttributeException(e);
        } catch (MBeanException e) {
            throw new ProxyAttributeException(e);
        } catch (ReflectionException e) {
            throw new ProxyAttributeException(e);
        } catch (IOException e) {
            throw new ProxyAttributeException(e);
        } catch (InstantiationException e) {
            throw new ProxyAttributeException(e);
        } catch (IllegalAccessException e) {
            throw new ProxyAttributeException(e);
        }
    }

    protected Object getAttribute(String attributeName) {
        try {
            return connection.getAttribute(name, attributeName);
        } catch (AttributeNotFoundException e) {
            throw new ProxyAttributeException(e);
        } catch (InstanceNotFoundException e) {
            throw new ProxyAttributeException(e);
        } catch (MBeanException e) {
            throw new ProxyAttributeException(e);
        } catch (ReflectionException e) {
            throw new ProxyAttributeException(e);
        } catch (IOException e) {
            throw new ProxyAttributeException(e);
        }
    }

}
