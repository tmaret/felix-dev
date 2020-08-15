/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.felix.http.jetty.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.jetbrains.annotations.NotNull;

import static java.lang.Long.MAX_VALUE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Unbounded pool of virtual threads.
 *
 * @see <a href="https://openjdk.java.net/projects/loom">Loom - Fibers, Continuations and Tail-Calls for the JVM</a>
 */
public class VirtualThreadPool extends ContainerLifeCycle implements ThreadPool {

    private volatile ExecutorService executor;

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        executor = Executors.newVirtualThreadExecutor();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        executor.shutdownNow();
    }

    @Override
    public void execute(@NotNull Runnable command) {
        executor.execute(command);
    }

    @Override
    public void join() throws InterruptedException {
        executor.awaitTermination(MAX_VALUE, MILLISECONDS);
    }

    @Override
    public int getThreads() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getIdleThreads() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isLowOnThreads() {
        return false;
    }
}
