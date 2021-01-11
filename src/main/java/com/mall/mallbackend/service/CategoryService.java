package com.mall.mallbackend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Controller;

import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.Category;
import com.mall.mallbackend.repository.CategoryRepository;

@Controller
public class CategoryService {
	
	private final CategoryRepository repository;
	public CategoryService(CategoryRepository repository) {
		this.repository = repository;
	}
	
	public ServerResponse<List<Integer> > selectCategoryAndChildrenById(Integer categoryId) {
		Set<Category> categorySet = new HashSet<>();
		findChildCategory(categorySet, categoryId);
		
		List<Integer> categoryIdList = new ArrayList<>();
		if(categoryId != null) {
			for(Category item: categorySet) {
				categoryIdList.add(item.getId());
			}
		}
		return ServerResponse.createBySuccess(categoryIdList);
	}
	
	private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
		Optional<Category> category = repository.findById(categoryId);
		if(!category.isEmpty()) {
			categorySet.add(category.get());
		}
		
		// Find child category
		List<Category> categoryList = repository.findByParentId(categoryId);
		for(Category item: categoryList) {
			findChildCategory(categorySet, item.getId());
		}
		return categorySet;
	}
	
	public Optional<Category> findById(Integer categoryId) {
		return repository.findById(categoryId);
	}
}
