package com.mall.mallbackend.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mall.mallbackend.model.User;


public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
	Integer countByUsername(String username);

	User findByUsernameAndPassword(String username, String md5Pass);

	Integer countByEmail(String str);

}
