package com.mall.mallbackend.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import com.mall.mallbackend.common.Const;
import com.mall.mallbackend.common.PageInfo;
import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.User;
@Controller
public class UserService {
	/**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse<?> checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
    
    public ServerResponse<PageInfo<User, User> > assembleUserPageInfo(Page<User> pagedResult){
		PageInfo<User, User> pageInfo;
    	if(pagedResult.hasContent()) {
    		System.out.println(pagedResult.getSize());
    		for(User user: pagedResult.getContent()) {
    			user.setPassword("");
    		}
			// Assemble as PageInfo
			pageInfo = new PageInfo<User, User>(pagedResult, pagedResult.getContent(), "");
			return ServerResponse.createBySuccess(pageInfo);
    	} else {
        	return ServerResponse.createBySuccess(new PageInfo<User, User>());
        }
	}
    
}
