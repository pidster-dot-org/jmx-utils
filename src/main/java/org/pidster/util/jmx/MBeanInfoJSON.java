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

import javax.management.MBeanInfo;


/**
 * @author <a href="http://pidster.com/">pidster</a>
 *
 */
public class MBeanInfoJSON {

    private MBeanInfo mbean;

    public MBeanInfoJSON(MBeanInfo mbean) {
        this.mbean = mbean;
    }

    public String toJSON() {
        StringBuilder s = new StringBuilder();

        s.append("{");

        String[] fields = mbean.getDescriptor().getFields();
        boolean first = true;
        for (String fieldName : fields) {
            if (!first) {
                s.append(", ");
            }
            else {
                first = false;
            }

            System.out.println("fieldName: " + fieldName);

            Object value;
            if (fieldName.indexOf("=") > -1) {
                String[] pair = fieldName.split("=");
                fieldName = pair[0];
                value = pair[1];
            }
            else {
                value = mbean.getDescriptor().getFieldValue(fieldName);
            }

            s.append("\"");
            s.append(fieldName);
            s.append("\":\"");
            s.append(value);
            s.append("\"");
        }
        s.append("}");

        return s.toString();
    }

}
