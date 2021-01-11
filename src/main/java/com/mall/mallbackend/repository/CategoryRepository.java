package com.mall.mallbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mall.mallbackend.model.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
	List<Category> findByParentId(Integer categoryId);
	
	
}
