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
package com.generallycloud.baseio.sample.http11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.generallycloud.baseio.codec.http11.future.HttpReadFuture;
import com.generallycloud.baseio.common.EmojiUtil;
import com.generallycloud.baseio.common.Encoding;
import com.generallycloud.baseio.container.http11.HtmlUtil;
import com.generallycloud.baseio.container.http11.HttpSession;
import com.generallycloud.baseio.container.http11.service.HttpFutureAcceptorService;

public class TestEmojiServlet extends HttpFutureAcceptorService {

	@Override
	protected void doAccept(HttpSession session, HttpReadFuture future) throws Exception {

		String emoji = "🏠🏡🏫🏢🏣🏥🏦🏪🏩🏨💒⛪️🏬🏤🌇🌆🌄🗻🗾🗼🏭⛺️🏰🏯😊😍😘😳😡😓😭😲😁😱😖😉😏😜😰😢😚😄😪😣😔😠😌😝😂😥😃😨😒😷😞👿👽😁😄😇😯😕😂😅😈";

		emoji = EmojiUtil.EMOJI_ALL;

		StringBuilder builder = new StringBuilder(HtmlUtil.HTML_HEADER);

		builder.append(
				"<div id=\"container\" style=\"width: 90%;margin-left: auto;margin-right: auto;margin-top: 10px;font-size: 26px;color: rgb(175, 46, 46);\">\n");

		List<String> emojiList = EmojiUtil.bytes2Emojis(emoji.getBytes(Encoding.UTF8));
		
		List<String> rgbList = getRGBList(String.valueOf(new Random().nextInt(56)+200), emojiList.size());
		
		for (int i = 0; i < emojiList.size(); i++) {
			builder.append("<span style=\"color:"+rgbList.get(i)+";\">");
			builder.append(emojiList.get(i));
			builder.append("</span>\n");
		}
		
		builder.append("</div>\n");
		builder.append(HtmlUtil.HTML_POWER_BY);
		builder.append(HtmlUtil.HTML_BOTTOM);

		future.write(builder.toString());

		future.setResponseHeader("Content-Type", "text/html");

		session.flush(future);
	}

	private List<String> getRGBList(String r, int size) {
		List<String> list = new ArrayList<>(size);
		int i = 0;
		int b = 255;
		for (int g = 255; g > 0;) {
			if (b < 100) {
				b = 0;
				for (; b <= 255;b += 3) {
					if (i++ > size) {
						return list;
					}
					list.add("rgb(" + r + "," + g + "," + b + ")");
				}
			}else{
				b = 255;
				for (; b >= 0;b -= 3) {
					if (i++ > size) {
						return list;
					}
					list.add("rgb(" + r + "," + g + "," + b + ")");
				}
			}
			g -= 3;
		}

		return list;
	}

}
