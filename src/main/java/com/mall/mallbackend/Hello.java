package com.mall.mallbackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {
	@RequestMapping(name="/hello",method=RequestMethod.GET)
	public String hello() {
		System.out.println("hello");
		return "hello sptingboot";
	}
}
