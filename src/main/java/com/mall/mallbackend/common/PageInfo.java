package com.mall.mallbackend.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

// T: type of Page, E: type of VO
public class PageInfo<T,E> implements Serializable {
	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public List<E> getList() {
		return list;
	}
	public void setList(List<E> list) {
		this.list = list;
	}
	private Integer pageNum;
	private Integer pageSize;
	private Long total;
	private String orderBy;
	private List<E> list;
	public PageInfo() {
		this.pageNum = 0;
		this.pageSize = 0;
		this.total = (long) 0;
		this.orderBy = "";
		this.list = new ArrayList<>();
	}
	public PageInfo(Page<T> page, List<E> list, String orderBy) {
		this.pageNum = page.getPageable().getPageNumber();
		this.pageSize = page.getTotalPages();
		this.total = page.getTotalElements();
		this.orderBy = orderBy;
		this.list = list;
	}
}
