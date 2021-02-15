package com.mall.mallbackend.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.mall.mallbackend.model.Order;
import com.mall.mallbackend.vo.OrderVo;

public interface OrderRepository extends PagingAndSortingRepository<Order, Integer>{

	@SuppressWarnings("unchecked")
	Order save(Order order);

	Page<Order> findByUserId(Integer userId, Pageable paging);

	long count();
}
