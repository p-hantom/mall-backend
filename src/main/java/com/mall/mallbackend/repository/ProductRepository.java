package com.mall.mallbackend.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mall.mallbackend.model.Category;
import com.mall.mallbackend.model.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>{
//	public static final List<String> Column_List = new ArrayList<>();
//	Column_List.add("id");
	// ToDO
	public static final String Column_String = "";
	public static final String GetProductByKeywordCategory = 
			"SELECT p FROM Product p WHERE p.name LIKE keyword";
	@Transactional(readOnly = true)
	Optional<Product> findById(Integer id);
	
	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.categoryId in :categoryIdList")
	@Transactional(readOnly = true)
	Page<Product> findByNameAndCategoryIds(@Param("keyword") String keyword, @Param("categoryIdList") List<Integer> categoryIdList, Pageable pageable);
	 
	// TODO: AND product.categoryId IN 
	//, @Param("categoryId") Integer categoryId,
	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
	@Transactional(readOnly = true)
	Page<Product> getProductByKeyword(@Param("keyword") String keyword, Pageable pageable);

	Page<Product> findByNameContaining(String keyword, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.name LIKE %:productName% OR p.id=:productId")
	@Transactional(readOnly = true)
	Page<Product> findByNameContainingAndId(@Param("productName") String productName,@Param("productId") Integer productId, Pageable pageable);

	Page<Product> findAll(Pageable paging);
	
	long count();
}
