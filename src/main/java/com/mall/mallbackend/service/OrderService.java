package com.mall.mallbackend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import com.mall.mallbackend.common.Const;
import com.mall.mallbackend.common.PageInfo;
import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.Cart;
import com.mall.mallbackend.model.Order;
import com.mall.mallbackend.model.OrderItem;
import com.mall.mallbackend.model.Product;
import com.mall.mallbackend.model.Shipping;
import com.mall.mallbackend.model.User;
import com.mall.mallbackend.repository.CartRepository;
import com.mall.mallbackend.repository.OrderItemRepository;
import com.mall.mallbackend.repository.OrderRepository;
import com.mall.mallbackend.repository.ProductRepository;
import com.mall.mallbackend.repository.ShippingRepository;
import com.mall.mallbackend.util.BigDecimalUtil;
import com.mall.mallbackend.util.DateTimeUtil;
import com.mall.mallbackend.util.PropertiesUtil;
import com.mall.mallbackend.vo.OrderItemVo;
import com.mall.mallbackend.vo.OrderVo;
import com.mall.mallbackend.vo.ShippingVo;

@Controller
public class OrderService {
	private final OrderRepository orders;
	private final OrderItemRepository orderItems;
	private final CartRepository carts;
	private final ProductRepository products;
	private final ShippingRepository shippings;
	
	public OrderService(OrderRepository orders, CartRepository carts, ProductRepository products, ShippingRepository shippings, OrderItemRepository orderItems) {
		this.orders = orders;
		this.orderItems = orderItems;
		this.carts = carts;
		this.products = products;
		this.shippings = shippings;
	}

	public ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> cartList) {
		if(cartList.isEmpty()) {
			return ServerResponse.createByErrorMessage("The cart is empty.");
		}
		List<OrderItem> orderItemList = new ArrayList<>();
		
		//校验购物车的数据,包括产品的状态和数量
        for(Cart cartItem : cartList){
            OrderItem orderItem = new OrderItem();
            Optional<Product> optionalProduct = products.findById(cartItem.getProductId());
            Product product = optionalProduct.get();
            if(Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"不是在线售卖状态");
            }

            //校验库存
            if(cartItem.getQuantity() > product.getStock()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"库存不足");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
	}

	public BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
		BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
	}

	public Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
		Order order = new Order();
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);

        order.setUserId(userId);
        order.setShippingId(shippingId);
        //发货时间等等
        //付款时间等等
        Order savedOrder = orders.save(order);
        if(savedOrder!=null){
            return order;
        }
        return null;
	}
	
	private long generateOrderNo(){
        long currentTime =System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

	public void reduceProductStock(List<OrderItem> orderItemList) {
		for(OrderItem orderItem : orderItemList){
            Product product = products.findById(orderItem.getProductId()).get();
            product.setStock(product.getStock()-orderItem.getQuantity());
            products.save(product);
        }
	}

	public void cleanCart(List<Cart> cartList) {
		for(Cart cart : cartList){
            carts.deleteById(cart.getId());
        }
	}

	public OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
		OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        orderVo.setImageHost(PropertiesUtil.getProperty("file.prefix"));
        if(order.getShippingId()!=null) {
        	Optional<Shipping> optionalShipping = shippings.findById(order.getShippingId());
        	Shipping shipping = optionalShipping.isEmpty() ? null : optionalShipping.get();
        	if(shipping != null){
                orderVo.setReceiverName(shipping.getReceiverName());
                orderVo.setShippingVo(assembleShippingVo(shipping));
            }
        }
        
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

//        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        
        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        
        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
	}

	public OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
		OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
	}

	private ShippingVo assembleShippingVo(Shipping shipping) {
		ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
	}
	
	public List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList = new ArrayList<>();
        for(Order order : orderList){
            List<OrderItem>  orderItemList = new ArrayList<>();
            if(userId == null){
                //todo 管理员查询的时候 不需要传userId
            	orderItemList = orderItems.findByOrderNo(order.getOrderNo());
//                orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo());
            }else{
                orderItemList = orderItems.findByOrderNoAndUserId(order.getOrderNo(),userId);
            }
//            System.out.println("orderNo:"+order.getOrderNo());
            OrderVo orderVo = assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }
	
	public ServerResponse<PageInfo<Order, Order>> assembleOrderPageInfo(Page<Order> pagedResult) {
		PageInfo<Order, Order> pageInfo;
		if(pagedResult.hasContent()) {
			pageInfo = new PageInfo(pagedResult, pagedResult.getContent(), "");
			return ServerResponse.createBySuccess(pageInfo);
		}
		else {
			return ServerResponse.createBySuccess(new PageInfo<Order, Order>());
		}
	}
}
