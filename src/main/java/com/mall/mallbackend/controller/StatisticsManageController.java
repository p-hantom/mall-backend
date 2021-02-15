package com.mall.mallbackend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.Statistics;
import com.mall.mallbackend.repository.OrderRepository;
import com.mall.mallbackend.repository.ProductRepository;
import com.mall.mallbackend.repository.UserRepository;

@RestController
@RequestMapping(path="/manage/statistic")
public class StatisticsManageController {
	private final UserRepository users;
	private final ProductRepository products;
	private final OrderRepository orders;
	
	public StatisticsManageController(UserRepository users, ProductRepository products, OrderRepository orders) {
		this.users = users;
		this.products = products;
		this.orders = orders;
		
	}
	
	@PostMapping(path="/base_count.do")
	public ServerResponse<Statistics> getBaseCount(){
		long userCount = users.count();
		long productCount = products.count();
		long orderCount = orders.count();
		Statistics statistics = new Statistics(userCount, productCount, orderCount);
		
		return ServerResponse.createBySuccess(statistics);
	}
}
