package org.pidster.util.jmx;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class MBeanJSON implements JSON {

    private ObjectName name;

    private MBeanInfo info;

    private AttributeList list;

    public MBeanJSON(ObjectName name, MBeanInfo info, AttributeList list) {
        this.name = name;
        this.info = info;
        this.list = list;
    }

    @Override
    public String toJSON() {
        StringBuilder s = new StringBuilder();

        s.append("{\n");
        s.append("\t\"attributes\":{\n");

        boolean first = true;
        for (Attribute attr : list.asList()) {
            if (first) {
                first = false;
            }
            else {
                s.append(",\n");
            }
            s.append("\t\t\"");
            s.append(attr.getName());
            s.append("\":");
            Object value = attr.getValue();
            if (value == null) {
                s.append(value);
            }
            else {
                append(s, value);
            }
        }
        s.append("\n\t}\n");
        s.append("}");

        return s.toString();
    }

    @Override
    public String toString() {
        return "MBeanJSON [info=" + info + ", list=" + list + "]";
    }

    private static void append(StringBuilder s, Object value) {
        if (String.class.equals(value.getClass())) {
            s.append("\"");
            s.append(value);
            s.append("\"");
        }
        else if (Integer.class.equals(value.getClass())) {
            s.append(value);
        }
        else if (Long.class.equals(value.getClass())) {
            s.append(value);
        }
        else if (Boolean.class.equals(value.getClass())) {
            s.append(value);
        }
        else if (ObjectName.class.equals(value.getClass())) {
            s.append(((ObjectName) value).getCanonicalName());
        }
        else if (CompositeDataSupport.class.equals(value.getClass())) {
            CompositeDataSupport data = (CompositeDataSupport) value;
            s.append(data);
        }
        else if (Map.class.equals(value.getClass())) {
            s.append("{");
            @SuppressWarnings("unchecked")

            Map<Object, Object> map = (Map<Object, Object>) value;
            Set<Entry<Object, Object>> entrySet = map.entrySet();

            boolean first = true;
            for (Entry<Object, Object> e : entrySet) {
                if (first) {
                    first = false;
                }
                else {
                    s.append(",");
                }
                s.append("\"");
                s.append(e.getKey());
                s.append("\":");
                append(s, e.getValue());
            }
            s.append("}");
        }
        else if (TabularDataSupport.class.equals(value.getClass())) {
            s.append("{");

            TabularDataSupport map = (TabularDataSupport) value;
            Set<Entry<Object, Object>> entrySet = map.entrySet();

            boolean first = true;
            for (Entry<Object, Object> e : entrySet) {
                if (first) {
                    first = false;
                }
                else {
                    s.append(",");
                }
                s.append("\"");
                s.append(e.getKey());
                s.append("\":");
                append(s, e.getValue());
            }
            s.append("}");
        }
        else if (value.getClass().isArray()) {
            s.append("[");
            int length = Array.getLength(value);
            for (int i=0; i<length; i++) {
                if (i > 0) {
                    s.append(",");
                }
                Object o = Array.get(value, i);
                append(s, o);
            }
            s.append("]");
        }
        else {
            s.append(value.getClass());
        }
    }

}
