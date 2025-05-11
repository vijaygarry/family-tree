package com.neasaa.base.app.enums;

public enum ChannelEnum {
	/** Web browser from Desktop, MacBook etc. */
	WEB_BROWSER,
	/** Native mobile app */
	MOBILE_APP, 
	/** Mobile browser */
	MOBILE_BROWSER, 
	/** Command line */
	COMMAND_LINE;
	
	public static ChannelEnum getChannelFromString (String channelValue) {
		for(ChannelEnum channel : ChannelEnum.values()) {
			if(channel.name().equalsIgnoreCase(channelValue)) {
				return channel;
			}
		}
		return null;
	}
	
}
