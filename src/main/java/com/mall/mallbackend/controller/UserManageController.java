package com.mall.mallbackend.controller;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mall.mallbackend.common.Const;
import com.mall.mallbackend.common.PageInfo;
import com.mall.mallbackend.common.ResponseCode;
import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.Product;
import com.mall.mallbackend.model.User;
import com.mall.mallbackend.repository.ProductRepository;
import com.mall.mallbackend.repository.UserRepository;
import com.mall.mallbackend.service.ProductService;
import com.mall.mallbackend.service.UserService;
import com.mall.mallbackend.vo.ProductListVo;

@RestController
@RequestMapping(path="/manage/user")
public class UserManageController {
	private final UserRepository users;
	private final UserService userService;
	
	public UserManageController(UserRepository users, UserService userService) {
		this.users = users;
		this.userService = userService;
	}
	
	@PostMapping("list.do")
    public ServerResponse<PageInfo<User, User>> list(
    		HttpSession session,
    		@RequestParam(value = "pageNum",defaultValue = "0") int pageNum,
	        @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充业务
        	Pageable paging;
        	paging = PageRequest.of(pageNum, pageSize, Sort.unsorted());
        	// Find in database
        	
        	Page<User> pagedResult = users.findAll(paging);
        	
        	return userService.assembleUserPageInfo(pagedResult);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
	
	@PostMapping(path="/login.do")
	public ServerResponse<User> login(String username, String password, HttpSession session){
		ServerResponse<User> response = loginService(username, password);
		if(response.isSuccess()) {
			session.setAttribute(Const.CURRENT_USER,response.getData());
		}
		return response;
	}
	
	private ServerResponse<User> loginService(String username, String password){
		Integer count = users.countByUsername(username);
		if(count == 0) {
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		
		String md5Pass = DigestUtils.md5DigestAsHex(password.getBytes());
		User user = users.findByUsernameAndPassword(username, md5Pass);
		if(user == null) {
			return  ServerResponse.createByErrorMessage("密码错误");
		}
		
		user.setPassword("");
		return ServerResponse.createBySuccess("登录成功",user);
	}
}
