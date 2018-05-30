/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.generallycloud.baseio.component;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.generallycloud.baseio.protocol.ChannelFuture;
import com.generallycloud.baseio.protocol.Future;

/**
 * @author wangkai
 *
 */
public class SocketSessionManager {

    private Map<Integer, SocketSession> sessions         = new ConcurrentHashMap<>();
    private Map<Integer, SocketSession> readOnlySessions = Collections.unmodifiableMap(sessions);
    private ChannelContext              context;

    public SocketSessionManager(ChannelContext context) {
        this.context = context;
    }

    private AtomicInteger managedSessionSize = new AtomicInteger();

    public int getManagedSessionSize() {
        return managedSessionSize.get();
    }

    public SocketSession getSession(int sessionId) {
        return sessions.get(sessionId);
    }

    public void putSession(SocketSession session) {
        sessions.put(session.getSessionId(), session);
        managedSessionSize.incrementAndGet();
    }

    public void removeSession(SocketSession session) {
        if (sessions.remove(session.getSessionId()) != null) {
            managedSessionSize.decrementAndGet();
        }
    }

    public void broadcast(Future future) throws IOException {
        broadcast(future, sessions.values());
    }

    public void broadcastChannelFuture(ChannelFuture future) {
        broadcastChannelFuture(future, sessions.values());
    }

    public void broadcast(Future future, Collection<SocketSession> sessions) throws IOException {
        if (sessions.size() == 0) {
            return;
        }
        SocketChannel channel = context.getSimulateSocketChannel();
        ChannelFuture f = (ChannelFuture) future;
        context.getProtocolCodec().encode(channel, f);
        broadcastChannelFuture(f, sessions);
    }

    public void broadcastChannelFuture(ChannelFuture future, Collection<SocketSession> sessions) {
        if (sessions.size() == 0) {
            return;
        }
        for (SocketSession s : sessions) {
            s.flushChannelFuture(future.duplicate());
        }
    }

    public Map<Integer, SocketSession> getManagedSessions() {
        return readOnlySessions;
    }

}
