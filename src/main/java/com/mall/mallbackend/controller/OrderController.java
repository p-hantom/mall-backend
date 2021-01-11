package com.mall.mallbackend.controller;

import java.math.BigDecimal;
import java.util.List;

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
import com.mall.mallbackend.model.Cart;
import com.mall.mallbackend.model.Order;
import com.mall.mallbackend.model.OrderItem;
import com.mall.mallbackend.model.Product;
import com.mall.mallbackend.model.User;
import com.mall.mallbackend.repository.CartRepository;
import com.mall.mallbackend.repository.OrderItemRepository;
import com.mall.mallbackend.repository.OrderRepository;
import com.mall.mallbackend.repository.ProductRepository;
import com.mall.mallbackend.repository.ShippingRepository;
import com.mall.mallbackend.service.OrderService;
import com.mall.mallbackend.vo.OrderVo;
import com.mall.mallbackend.vo.ProductListVo;

@RestController
@RequestMapping(path="/order")
public class OrderController {
	private final OrderRepository orders;
	private final CartRepository carts;
	private final OrderService orderService;
	private final ProductRepository products;
	private final OrderItemRepository orderItems;
	private final ShippingRepository shippings;
	public OrderController(OrderRepository orders, CartRepository carts, ProductRepository products, OrderService orderService,OrderItemRepository orderItems,ShippingRepository shippings) {
		this.orders = orders;
		this.carts = carts;
		this.products = products;
		this.orderService = new OrderService(orders, carts, products, shippings, orderItems);
		this.shippings = shippings;
		this.orderItems = orderItems;
	}
	
	@PostMapping(path="/create.do")
	public ServerResponse create(HttpSession session, Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        Integer userId = user.getId();
        if(shippingId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        
        // Find cart list by userId
        List<Cart> cartList = carts.findByUserId(userId);
        
        ServerResponse<List<OrderItem> > serverResponse = orderService.getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = serverResponse.getData();
        BigDecimal payment = orderService.getOrderTotalPrice(orderItemList);
        
        if(orderItemList.isEmpty()) {
        	return ServerResponse.createByErrorMessage("购物车为空");
        }
        
        // Generate order
        Order order = orderService.assembleOrder(userId,shippingId,payment);
        if(order == null){
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        for(OrderItem orderItem : orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        orderItems.saveAll(orderItemList);
        
        //生成成功,我们要减少我们产品的库存
        orderService.reduceProductStock(orderItemList);
        //清空一下购物车
        orderService.cleanCart(cartList);

        //返回给前端数据

        OrderVo orderVo = orderService.assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }
	
	@PostMapping(path="/list.do")
	public ServerResponse<PageInfo<Order,OrderVo> > list(HttpSession session, 
			@RequestParam(value = "pageNum",defaultValue = "0") int pageNum, 
			@RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        Integer userId = user.getId();
        System.out.println(userId);
        
        Pageable paging = PageRequest.of(pageNum, pageSize, Sort.unsorted());;
		PageInfo<Order, OrderVo> pageInfo;
		Page<Order> pagedResult = orders.findByUserId(userId, paging);
		
		if(pagedResult.hasContent()) {
			List<Order> orderList = pagedResult.getContent();
			List<OrderVo> orderVoList = orderService.assembleOrderVoList(orderList, userId);
			pageInfo = new PageInfo<>(pagedResult, orderVoList, "");
			return ServerResponse.createBySuccess(pageInfo);
		} else {
        	System.out.println("empty list");
        	return ServerResponse.createBySuccess(new PageInfo<Order,OrderVo>());
        }
	}
	
//	@PostMapping(path="/get_order_cart_product.do")
//	public ServerResponse getOrderCartProduct(HttpSession session) {
//		User user = (User)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        Integer userId = user.getId();
//	}
}
