package com.mall.mallbackend.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mall.mallbackend.model.Order;
import com.mall.mallbackend.model.OrderItem;

public interface OrderItemRepository extends PagingAndSortingRepository<OrderItem, Integer>{

	List<OrderItem> findByOrderNo(Long orderNo);

	List<OrderItem> findByOrderNoAndUserId(Long orderNo, Integer userId);

//	void saveAll(List<OrderItem> orderItemList);

}
