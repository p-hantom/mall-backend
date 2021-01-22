package com.mall.mallbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mall.mallbackend.model.Cart;
import com.mall.mallbackend.model.Product;

public interface CartRepository extends PagingAndSortingRepository<Cart, Integer>{

	List<Cart> findByUserId(Integer userId);

	@Query("SELECT COUNT(c) FROM Cart c WHERE c.userId=:userId AND c.checked=0")
	@Transactional(readOnly = true)
	Long selectCartProductCheckedStatusByUserId(@Param("userId") Integer userId);

	@Query("SELECT new Cart(c.id,c.userId,c.productId,c.quantity,c.checked,c.createTime,c.updateTime) FROM Cart c WHERE c.userId=:userId AND c.checked=1 AND c.quantity>0")
	@Transactional(readOnly = true)
	List<Cart> selectCheckedCartByUserId(@Param("userId") Integer userId);
	
	Optional<Cart> findByUserIdAndProductId(Integer userId, Integer productId);

	@Transactional
	void deleteByUserIdAndProductId(Integer userId, Integer prdId);

	@Query("SELECT SUM(c.quantity) FROM Cart c WHERE c.userId=:userId")
	@Transactional(readOnly = true)
	Long selectSumByUserId(@Param("userId") Integer userId);

	@Modifying
	@Transactional
	@Query("UPDATE Cart c SET c.checked=1 WHERE c.userId=:userId")
	void updateAllChecked(@Param("userId") Integer userId);

	@Modifying
	@Transactional
	@Query("UPDATE Cart c SET c.checked=0 WHERE c.userId=:userId")
	void updateAllUnchecked(Integer userId);

}
