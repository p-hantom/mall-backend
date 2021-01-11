package com.mall.mallbackend.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mall.mallbackend.common.Const;
import com.mall.mallbackend.common.ResponseCode;
import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.Shipping;
import com.mall.mallbackend.model.User;
import com.mall.mallbackend.repository.ShippingRepository;

@RestController
@RequestMapping(path="/shipping")
public class ShippingController {
	private final ShippingRepository shippings;
	public ShippingController(ShippingRepository shippings) {
		this.shippings = shippings;
	}
	
	@PostMapping(path="/add.do")
	public ServerResponse<Map<String, Integer>> add(HttpSession session,Shipping shipping){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        shipping.setUserId(user.getId());
        Shipping savedShipping = shippings.save(shipping);
        if(savedShipping!=null) {
        	Map<String, Integer> result = new HashMap<>();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }
}
