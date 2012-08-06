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

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;


/**
 * @author <a href="http://pidster.com/">pidster</a>
 *
 */
public class JMXConnectorServers {

    public static JMXConnectorServer startServer(String hostname, int port) {
        try {
            JMXServiceURL serviceURL = JMXServiceURLs.serviceURL(hostname, port);
            return startServer(serviceURL, new HashMap<String, Object>());
        } catch (MalformedURLException e) {
            throw new ServerException(e);
        }
    }

    public static JMXConnectorServer startServer(String hostname, int port, File passwordFile, File accessFile) {
        try {
            JMXServiceURL serviceURL = JMXServiceURLs.serviceURL(hostname, port);

            if (passwordFile == null || !passwordFile.exists()) {
                throw new ServerException("Password file is null or missing: " + passwordFile.getAbsolutePath());
            }

            if (accessFile == null || !accessFile.exists()) {
                throw new ServerException("Access file is null or missing: " + accessFile.getAbsolutePath());
            }

            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("jmx.remote.x.password.file", passwordFile.getAbsolutePath());
            attributes.put("jmx.remote.x.access.file", accessFile.getAbsolutePath());

            return startServer(serviceURL, attributes);
        } catch (MalformedURLException e) {
            throw new ServerException(e);
        }
    }

    public static JMXConnectorServer startServer(String hostname, int port, File ldapConfig, String ldapConfigEntry) {
        try {
            JMXServiceURL serviceURL = JMXServiceURLs.serviceURL(hostname, port);

            if (ldapConfig == null || !ldapConfig.exists()) {
                throw new ServerException("LDAP config file is null or missing: " + ldapConfig.getAbsolutePath());
            }

            System.setProperty("java.security.auth.login.config", ldapConfig.getAbsolutePath());
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("jmx.remote.x.login.config", ldapConfigEntry);

            return startServer(serviceURL, attributes);
        } catch (MalformedURLException e) {
            throw new ServerException(e);
        }
    }

    public static JMXConnectorServer startServer(JMXServiceURL serviceURL, Map<String, Object> attributes) {
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer(); 
            JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(serviceURL, attributes, mbeanServer);
            connectorServer.start();
            return connectorServer;

        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

}
