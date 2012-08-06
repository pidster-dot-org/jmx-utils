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

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.tools.attach.VirtualMachine;


/**
 * @author <a href="http://pidster.com/">pidster</a>
 *
 */
public class Closer {

    private static final Logger LOG = Logger.getLogger(Closer.class.getName());

    public static void close(Closeable c) {
        if (c == null) {
            return;
        }

        try {
            c.close();
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Woah dude!", e);
        }
    }

    public static void detach(VirtualMachine machine) {
        if (machine == null) {
            return;
        }

        try {
            machine.detach();
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Woah dude!", e);
        }
    }

}
