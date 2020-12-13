package com.mall.mallbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mall.mallbackend.model.Product;
import com.mall.mallbackend.repository.ProductRepository;


@RestController
@RequestMapping(path="/product")
public class ProductController {
	private final ProductRepository products;
	
	public ProductController(ProductRepository products) {
		this.products = products;
	}
	
	@GetMapping(path="/detail/{productId}")
	public Product getProduct(@PathVariable("productId") int prdId) {
		System.out.println(this.products.findById(prdId));
		return this.products.findById(prdId);
	}
}
