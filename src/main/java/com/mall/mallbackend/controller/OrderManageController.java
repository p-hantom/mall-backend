package com.mall.mallbackend.controller;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mall.mallbackend.common.Const;
import com.mall.mallbackend.common.PageInfo;
import com.mall.mallbackend.common.ResponseCode;
import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.Order;
import com.mall.mallbackend.model.Product;
import com.mall.mallbackend.model.User;
import com.mall.mallbackend.repository.OrderRepository;
import com.mall.mallbackend.service.OrderService;
import com.mall.mallbackend.service.UserService;
import com.mall.mallbackend.vo.ProductListVo;

@RestController
@RequestMapping(path="/manage/order")
public class OrderManageController {
	
	private final UserService userService;
	private final OrderRepository orders;
	private final OrderService orderService;
	
	public OrderManageController(UserService userService, OrderRepository orders, OrderService orderService) {
		this.userService = userService;
		this.orders = orders;
		this.orderService = orderService;
		
	}
	
	@PostMapping("list.do")
    public ServerResponse<PageInfo<Order, Order>> list(
    		HttpSession session,
    		@RequestParam(value = "pageNum",defaultValue = "0") int pageNum,
	        @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()){
        	Pageable paging;
        	paging = PageRequest.of(pageNum, pageSize, Sort.unsorted());
        	// Find in database
        	
        	Page<Order> pagedResult = orders.findAll(paging);
        	
        	return orderService.assembleOrderPageInfo(pagedResult);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
}
