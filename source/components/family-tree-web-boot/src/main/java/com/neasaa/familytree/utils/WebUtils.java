package com.neasaa.familytree.utils;

import com.neasaa.base.app.enums.ChannelEnum;
import com.neasaa.base.app.operation.OperationContext.ClientInformation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

@Log4j2
public class WebUtils {
	
	private static UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(1000).build();
	
	/**
	 * "request.getRemoteAddr()" won't work reliably behind proxies, as you'll get the proxy server’s IP.
	 * To get the actual caller IP address, first get the IP from header, if IP not found in header then get from request.
	 * @param request
	 * @return
	 */
	public static String getClientIp(HttpServletRequest request) {
	    String ip = request.getHeader("X-Forwarded-For");
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	    } else {
	        // In case of multiple IPs, the first one is the original client
	        ip = ip.split(",")[0].trim();
	    }
	    return "Client IP: " + ip;
	}
	
	public static ClientInformation getClientInformation (HttpServletRequest httpRequest) {
		
		UserAgent agent = userAgentAnalyzer.parse(httpRequest.getHeader("User-Agent"));
		ChannelEnum channel = ChannelEnum.WEB_BROWSER;
		String browserName = agent.getValue("AgentName");
		String browserVersion = agent.getValue("AgentVersion");
		String operatingSystem = agent.getValue("OperatingSystemNameVersion");
		String deviceType = agent.getValue("DeviceName");
		if(deviceType != null) {
			switch(deviceType.toLowerCase()) {
			case "mobile": 
			case "tablet": channel = ChannelEnum.MOBILE_BROWSER; break;
			case "desktop": channel = ChannelEnum.WEB_BROWSER; break;
			default:
				log.info("Unknown device type, defaulting to web browesr");
				channel = ChannelEnum.WEB_BROWSER;
			}
			
		}
		return ClientInformation.builder()
			.clientUserIpAddr(getClientIp(httpRequest))
			.channel(channel)
			.browserName(browserName)
			.browserVersion(browserVersion)
			.operatingSystem(operatingSystem)
			.deviceType(deviceType)
			.build();
	}
}
