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
package com.generallycloud.test.io.load.fixedlength;

import java.util.ArrayList;
import java.util.List;

import com.generallycloud.baseio.codec.fixedlength.FixedLengthCodec;
import com.generallycloud.baseio.codec.fixedlength.FixedLengthFuture;
import com.generallycloud.baseio.component.ChannelAcceptor;
import com.generallycloud.baseio.component.ChannelContext;
import com.generallycloud.baseio.component.ChannelEventListenerAdapter;
import com.generallycloud.baseio.component.IoEventHandle;
import com.generallycloud.baseio.component.LoggerChannelOpenListener;
import com.generallycloud.baseio.component.NioEventLoopGroup;
import com.generallycloud.baseio.component.NioSocketChannel;
import com.generallycloud.baseio.configuration.Configuration;
import com.generallycloud.baseio.protocol.Future;

public class TestLoadServer {

    public static void main(String[] args) throws Exception {

        final boolean batchFlush = true;

        NioEventLoopGroup group = new NioEventLoopGroup(8);
        group.setMemoryPoolCapacity(2560000 / 2);
        group.setBufRecycleSize(1024 * 64);
        group.setMemoryPoolUnit(128);
        Configuration c = new Configuration(8300);
        ChannelContext context = new ChannelContext(c);
        ChannelAcceptor acceptor = new ChannelAcceptor(context, group);
        context.setProtocolCodec(new FixedLengthCodec());
        context.addChannelEventListener(new LoggerChannelOpenListener());
        context.addChannelEventListener(new ChannelEventListenerAdapter() {

            @Override
            public void channelOpened(NioSocketChannel channel) throws Exception {
                channel.setIoEventHandle(new IoEventHandle() {
                    boolean      addTask = true;
                    List<Future> fs      = new ArrayList<>(1024 * 4);
                    @Override
                    public void accept(NioSocketChannel channel, Future future) throws Exception {
                        FixedLengthFuture f = (FixedLengthFuture) future;
                        f.write(f.getReadText(), channel);
                        if (batchFlush) {
                            fs.add(f);
                            if (addTask) {
                                addTask = false;
                                channel.getEventLoop().dispatchAfterLoop((e) -> {
                                    channel.flush(fs);
                                    addTask = true;
                                    fs.clear();
                                });
                            }
                        } else {
                            channel.flush(future);
                        }
                    }
                });
            }
        });
        acceptor.bind();
    }

}
