package com.mall.mallbackend.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mall.mallbackend.model.Shipping;

public interface ShippingRepository extends PagingAndSortingRepository<Shipping, Integer>{

}
