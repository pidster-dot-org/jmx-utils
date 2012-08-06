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
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;


/**
 * @author <a href="http://pidster.com/">pidster</a>
 *
 */
public class JMXConnectorClients {

    private static final String JMX_REMOTE_CREDENTIALS = "jmx.remote.credentials";

    private static final String LOCAL_CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";    

    private static final String MANAGEMENT_AGENT_JAR_FORMAT = "%1$s%2$slib%2$smanagement-agent.jar";

    public static JMXConnector attach(String pid) {
        VirtualMachine machine = null;
        
        try {
            machine = VirtualMachine.attach(pid);
            String javaHome = System.getProperty("java.home");

            if (!machine.getAgentProperties().contains(LOCAL_CONNECTOR_ADDRESS)) {
                String agent = String.format(MANAGEMENT_AGENT_JAR_FORMAT, javaHome, File.separator);

                File agentFile = new File(agent);
                if (agentFile == null || !agentFile.exists()) {
                    throw new ClientException("Agent JAR not found: " + agentFile.getAbsolutePath());
                }

                try {
                    machine.loadAgent(agent);
                } catch (AgentLoadException e) {
                    throw new ClientException(e);
                } catch (AgentInitializationException e) {
                    throw new ClientException(e);
                }
            }

            String serviceURL = machine.getAgentProperties().getProperty(LOCAL_CONNECTOR_ADDRESS);
            return connect(serviceURL);

        } catch (AttachNotSupportedException e) {
            throw new ClientException(e);
        } catch (IOException e) {
            throw new ClientException(e);
        }
        finally {
            Closer.detach(machine);
        }
    }

    public static JMXConnector connect(String url) {
        try {
            JMXServiceURL serviceURL = JMXServiceURLs.serviceURL(url);
            return JMXConnectorFactory.connect(serviceURL, null);

        } catch (MalformedURLException e) {
            throw new ClientException(e);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    public static JMXConnector connect(String hostname, int port) {
        try {
            JMXServiceURL serviceURL = JMXServiceURLs.serviceURL(hostname, port);
            return JMXConnectorFactory.connect(serviceURL, null);

        } catch (MalformedURLException e) {
            throw new ClientException(e);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    public static JMXConnector connect(String hostname, int port, String username, String password) {
        try {
            JMXServiceURL serviceURL = JMXServiceURLs.serviceURL(hostname, port);
            Map<String, Object> env = new HashMap<String, Object>();
            String[] credentials = new String[] { username, password };
            env.put(JMX_REMOTE_CREDENTIALS, credentials);

            JMXConnector connector = JMXConnectorFactory.newJMXConnector(serviceURL, env);
            connector.connect(env);
            return connector;

        } catch (MalformedURLException e) {
            throw new ClientException(e);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }


    public static JMXConnector connect(String hostname, int port, String path, String username, String password) {
        try {
            JMXServiceURL serviceURL = JMXServiceURLs.serviceURL(hostname, port, path);
            Map<String, Object> env = new HashMap<String, Object>();
            String[] credentials = new String[] { username, password };
            env.put(JMX_REMOTE_CREDENTIALS, credentials);

            JMXConnector connector = JMXConnectorFactory.newJMXConnector(serviceURL, env);
            connector.connect(env);
            return connector;

        } catch (MalformedURLException e) {
            throw new ClientException(e);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

}
