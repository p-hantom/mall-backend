package com.mall.mallbackend.repository;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mall.mallbackend.model.Product;

public interface ProductRepository extends Repository<Product, Integer>{
	@Transactional(readOnly = true)
	Product findById(Integer id);
}
