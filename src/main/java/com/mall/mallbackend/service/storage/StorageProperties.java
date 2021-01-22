package com.mall.mallbackend.service.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.mall.mallbackend.util.PropertiesUtil;

@ConfigurationProperties("storage")
public class StorageProperties {

	/**
	 * Folder location for storing files
	 */
	private String location = PropertiesUtil.getProperty("file.location");
	private String prefix = PropertiesUtil.getProperty("file.prefix");

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}