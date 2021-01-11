package com.mall.mallbackend.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mall.mallbackend.common.Const;
import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.User;
import com.mall.mallbackend.repository.UserRepository;

@RestController
@RequestMapping(path="/user")
public class UserController {
	private final UserRepository users;
	
	public UserController(UserRepository users) {
		this.users = users;
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
	
	@PostMapping(path="/register.do")
	public ServerResponse<User> register( User user
//			@RequestParam(value="username") String username, 
//			@RequestParam(value="password") String password,
//			@RequestParam(value="email") String email,
//			@RequestParam(value="phone") String phone,
//			@RequestParam(value="question") String question,
//			@RequestParam(value="answer") String answer
			){
		ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        
        //MD5加密
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		
		User insertResult = users.save(user);
		if(insertResult == null) {
			return ServerResponse.createByErrorMessage("Registration failed");
		}
		return ServerResponse.createBySuccessMessage("Registration succeeded");		
	}
	
	public ServerResponse<String> checkValid(String str, String type){
		if(StringUtils.isNotBlank(type)) {
			if(Const.USERNAME.equals(type)) {
				Integer count = users.countByUsername(str);
				if(count > 0) {
					return ServerResponse.createByErrorMessage("User already exists!");
				}
			}
			if(Const.EMAIL.equals(type)) {
				Integer count = users.countByEmail(str);
				if(count > 0) {
					return ServerResponse.createByErrorMessage("Email already exists!");
				}
			}
		} else{
            return ServerResponse.createByErrorMessage("Wrong parameters");
        }
        return ServerResponse.createBySuccessMessage("Checked successfully");
	}
	
	@PostMapping(path="/logout.do")
	public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }
}
