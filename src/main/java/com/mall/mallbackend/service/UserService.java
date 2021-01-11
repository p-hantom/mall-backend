package com.mall.mallbackend.service;

import org.springframework.stereotype.Controller;

import com.mall.mallbackend.common.Const;
import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.User;
@Controller
public class UserService {
	/**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
    
}
