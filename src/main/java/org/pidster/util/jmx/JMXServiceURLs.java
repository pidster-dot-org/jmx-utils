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

import java.net.MalformedURLException;

import javax.management.remote.JMXServiceURL;


/**
 * @author <a href="http://pidster.com/">pidster</a>
 *
 */
public class JMXServiceURLs {

    public static JMXServiceURL serviceURL(int port) throws MalformedURLException {
        return serviceURL("localhost", port, "/jmxrmi");
    }

    public static JMXServiceURL serviceURL(String hostname, int port) throws MalformedURLException {
        return serviceURL(hostname, port, "/jmxrmi");
    }

    public static JMXServiceURL serviceURL(String hostname, int port, String path) throws MalformedURLException {
        String url = String.format("service:jmx:rmi://%1$s:%2$d/jndi/rmi://%1$s:%2$d%3$s", hostname, port, path);
        return serviceURL(url);
    }

    public static JMXServiceURL serviceURL(String url) throws MalformedURLException {
        return new JMXServiceURL(url);
    }

}
