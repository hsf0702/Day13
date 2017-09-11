package com.example.a25737;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NewsInfoService {

	public static List<NewsInfo> getNewsInfos(InputStream is) {

		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(is, "utf-8");
			int type = parser.getEventType();
			List<NewsInfo> newsInfos = null;
			NewsInfo newsInfo = null;
			while (type != XmlPullParser.END_DOCUMENT) { 
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("news".equals(parser.getName())) { 
						newsInfos = new ArrayList<NewsInfo>();
					} else if ("newsInfo".equals(parser.getName())) {
						newsInfo = new NewsInfo();
					} else if ("icon".equals(parser.getName())) {
						String icon = parser.nextText();  
						newsInfo.setIconPath(icon);
					} else if ("title".equals(parser.getName())) {
						String title = parser.nextText();
						newsInfo.setTitle(title);
					} else if ("content".equals(parser.getName())) {
						String description = parser.nextText();
						newsInfo.setDescription(description);
					} else if ("type".equals(parser.getName())) {
						String newsType = parser.nextText();
						newsInfo.setType(Integer.parseInt(newsType));
					} else if ("comment".equals(parser.getName())) {
						String comment = parser.nextText();
						newsInfo.setComment(Long.parseLong(comment));
					}
					break;
				case XmlPullParser.END_TAG:
					if ("newsInfo".equals(parser.getName())) {
						newsInfos.add(newsInfo);
						newsInfo = null;
					}
					break;
				}
				type = parser.next();
			}
			return newsInfos;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
