package com.mall.mallbackend.model;

public class Statistics {
	private long userCount;
	private long productCount;
	private long orderCount;
	
	public Statistics(long userCount, long productCount, long orderCount) {
		this.userCount = userCount;
		this.productCount = productCount;
		this.orderCount = orderCount;
	}
	
	public long getUserCount() {
		return userCount;
	}
	public void setUserCount(long userCount) {
		this.userCount = userCount;
	}
	public long getProductCount() {
		return productCount;
	}
	public void setProductCount(long productCount) {
		this.productCount = productCount;
	}
	public long getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(long orderCount) {
		this.orderCount = orderCount;
	}
}
