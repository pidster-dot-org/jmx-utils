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

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;


/**
 * @author <a href="http://pidster.org/">pidster</a>
 *
 */
public class MBeanJSONFactory {

    public static MBeanJSON getMBeanJSON(ObjectName name, MBeanServerConnection connection) {

        try {
            MBeanInfo info = connection.getMBeanInfo(name);
            MBeanAttributeInfo[] attributes = info.getAttributes();

            AttributeList list = new AttributeList();
            for (MBeanAttributeInfo mai : attributes) {

                Object val;
                try {
                    val = connection.getAttribute(name, mai.getName());
                }
                catch (Exception e) {
                    Throwable t = e;
                    while (t.getCause() != null) {
                        t = t.getCause();
                    }
                    val = t.getMessage();
                }

                Attribute attr = new Attribute(mai.getName(), val);
                list.add(attr);
            }

            return new MBeanJSON(name, info, list);

        } catch (InstanceNotFoundException e) {
            throw new MBeanJSONException(e);
        } catch (IntrospectionException e) {
            throw new MBeanJSONException(e);
        } catch (ReflectionException e) {
            throw new MBeanJSONException(e);
        } catch (IOException e) {
            throw new MBeanJSONException(e);
        }
    }

}
