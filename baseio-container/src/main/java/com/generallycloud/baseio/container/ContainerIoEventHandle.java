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
package com.generallycloud.baseio.container;

import com.generallycloud.baseio.component.ChannelContext;
import com.generallycloud.baseio.component.IoEventHandle;
import com.generallycloud.baseio.component.NioSocketChannel;
import com.generallycloud.baseio.log.Logger;
import com.generallycloud.baseio.log.LoggerFactory;
import com.generallycloud.baseio.protocol.Future;

public abstract class ContainerIoEventHandle extends IoEventHandle {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void exceptionCaught(NioSocketChannel channel, Future future, Exception ex) {
        logger.error(ex.getMessage(), ex);
    }

    protected void initialize(ChannelContext context, boolean redeploy) throws Exception {}

    protected void destroy(ChannelContext context, boolean redeploy) {}

}
