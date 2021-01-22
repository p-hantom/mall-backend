package com.mall.mallbackend.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mall.mallbackend.common.Const;
import com.mall.mallbackend.common.ResponseCode;
import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.Cart;
import com.mall.mallbackend.model.Category;
import com.mall.mallbackend.model.Product;
import com.mall.mallbackend.model.User;
import com.mall.mallbackend.repository.CartRepository;
import com.mall.mallbackend.repository.ProductRepository;
import com.mall.mallbackend.service.CartService;
import com.mall.mallbackend.util.BigDecimalUtil;
import com.mall.mallbackend.vo.CartProductVo;
import com.mall.mallbackend.vo.CartVo;

@RestController
@RequestMapping(path="/cart")
public class CartController {
	private final CartRepository carts;
	private final ProductRepository products;
	private final CartService cartService;

	public CartController(CartRepository carts,ProductRepository products, CartService cartService) {
		this.carts = carts;
		this.products = products;
		this.cartService = new CartService(carts,products);
	}

	@PostMapping("list.do")
	public ServerResponse<CartVo> list(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        
        CartVo cartVo = cartService.getCartVo(user.getId());
        return ServerResponse.createBySuccess(cartVo);
	}
	
	@PostMapping("add.do")
	public ServerResponse<CartVo> add(@RequestParam(value="productId") Integer productId, 
			@RequestParam(value="count") Integer count,
			HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        Integer userId = user.getId();
        
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Optional<Cart> optionalCart = carts.findByUserIdAndProductId(userId, productId);
        
        if(optionalCart.isEmpty()){
            //这个产品不在这个购物车里,需要新增一个这个产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            carts.save(cartItem);
        }else{
            //这个产品已经在购物车里了.
            //如果产品已存在,数量相加
        	Cart cart = optionalCart.get();
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            carts.save(cart);
        }
        return ServerResponse.createBySuccess(cartService.getCartVo(userId));
	}
	
	@PostMapping("update.do")
	public ServerResponse<CartVo> update(@RequestParam(value="productId") Integer productId, 
			@RequestParam(value="count") Integer count,
			HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        Integer userId = user.getId();
        
        //Illegal argument
        if(productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //Find by userId and productId
        Optional<Cart> optionalCart = carts.findByUserIdAndProductId(userId, productId);
        if(!optionalCart.isEmpty()) {
        	Cart cart = optionalCart.get();
        	cart.setQuantity(count);
        	carts.save(cart);
        }
        return ServerResponse.createBySuccess(cartService.getCartVo(userId));
	}
	
	@PostMapping("delete_product.do")
	public ServerResponse<CartVo> deleteProduct(@RequestParam(value="productIds") String productIds, 
			HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        Integer userId = user.getId();
        List<String> productList = Arrays.asList(productIds.split(","));
        
        //Illegal argument
        if(productList.isEmpty()) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //Delete by userId and productId
        for(String productId: productList) {
        	Integer prdId = Integer.parseInt(productId);
        	carts.deleteByUserIdAndProductId(userId, prdId);
        }
        return ServerResponse.createBySuccess(cartService.getCartVo(userId));
	}
	
	@PostMapping("select.do")
	public ServerResponse<CartVo> select(@RequestParam(value="productId") Integer productId, 
			HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        Integer userId = user.getId();
        System.out.println("userId: "+userId);
        System.out.println("productId: "+productId);
        
        //Illegal argument
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //Find by userId and productId
        Optional<Cart> optionalCart = carts.findByUserIdAndProductId(userId, productId);
        if(!optionalCart.isEmpty()) {
        	Cart cart = optionalCart.get();
        	System.out.println("select cart: "+cart.getId());
        	cart.setChecked(Const.Cart.CHECKED);
        	carts.save(cart);
        }
        return ServerResponse.createBySuccess(cartService.getCartVo(userId));
	}
	
	@PostMapping("un_select.do")
	public ServerResponse<CartVo> unselect(@RequestParam(value="productId") Integer productId, 
			HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        Integer userId = user.getId();
        
        //Illegal argument
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //Find by userId and productId
        Optional<Cart> optionalCart = carts.findByUserIdAndProductId(userId, productId);
        if(!optionalCart.isEmpty()) {
        	Cart cart = optionalCart.get();
        	System.out.println("unselect cart: "+cart.getId());
        	cart.setChecked(Const.Cart.UN_CHECKED);
        	carts.save(cart);
        }
        return ServerResponse.createBySuccess(cartService.getCartVo(userId));
	}
	
	@PostMapping("get_cart_product_count.do")
	public ServerResponse<Long> getCartProductCount(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        Integer userId = user.getId();
        
        //Count by userId
        Long sum = carts.selectSumByUserId(userId);
        return ServerResponse.createBySuccess(sum);
	}
	
	@PostMapping("select_all.do")
	public ServerResponse<CartVo> selectAll(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        Integer userId = user.getId();
        
        carts.updateAllChecked(userId);
        return ServerResponse.createBySuccess(cartService.getCartVo(userId));
	}
	
	@PostMapping("un_select_all.do")
	public ServerResponse<CartVo> unselectAll(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        Integer userId = user.getId();
        
        carts.updateAllUnchecked(userId);
        return ServerResponse.createBySuccess(cartService.getCartVo(userId));
	}
}
