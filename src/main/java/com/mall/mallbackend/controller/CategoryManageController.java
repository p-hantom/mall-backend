package com.mall.mallbackend.controller;

import java.util.List;
import java.util.Optional;

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
import com.mall.mallbackend.common.ResponseCode;
import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.Category;
import com.mall.mallbackend.model.Product;
import com.mall.mallbackend.model.User;
import com.mall.mallbackend.repository.CategoryRepository;
import com.mall.mallbackend.service.CategoryService;
import com.mall.mallbackend.service.UserService;

@RestController
@RequestMapping(path="/manage/category")
public class CategoryManageController {
	private final UserService userService;
	private final CategoryRepository categorys;
	private final CategoryService categoryService;
	public CategoryManageController(UserService userService, CategoryRepository categorys, CategoryService categoryService) {
		this.userService = userService;
		this.categorys = categorys;
		this.categoryService = categoryService;
		
	}
	@PostMapping("get_category.do")
	public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session,
				@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充业务
        	List<Category> categoryList = categorys.findByParentId(categoryId);
        	if(categoryList.isEmpty()) {
        		return ServerResponse.createBySuccessMessage("No sub-categories found for the current category.");
        	}
        	return ServerResponse.createBySuccess(categoryList);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
	@PostMapping("get_category_list.do")
	public ServerResponse<List<Category>> getCategoryList(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充业务
        	List<Category> categoryList = (List<Category>) categorys.findAll();
        	return ServerResponse.createBySuccess(categoryList);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
	@PostMapping("get_deep_category.do")
	public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(HttpSession session,
				@RequestParam(value = "categoryId") Integer categoryId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充业务
        	
        	return categoryService.selectCategoryAndChildrenById(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
	@PostMapping("add_category.do")
	public ServerResponse<String> addCategory(HttpSession session,
				@RequestParam(value = "parentId" ,defaultValue = "0") Integer parentId,
				@RequestParam(value = "categoryName") String categoryName) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充业务
        	Category category = new Category();
            category.setName(categoryName);
            category.setParentId(parentId);
            category.setStatus(true);//这个分类是可用的
        	Category savedCategory = categorys.save(category);
        	if(savedCategory==null) {
        		return ServerResponse.createByErrorMessage("Failed to add a category.");
        	}
        	return ServerResponse.createBySuccess("Successfully added a category.");
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
	@PostMapping("set_category_name.do")
	public ServerResponse<String> setCategoryName(HttpSession session,
			@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId,
			@RequestParam(value = "categoryName") String categoryName) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充业务
        	Optional<Category> optionalCategory = categorys.findById(categoryId);
        	if(optionalCategory.isEmpty()) {
        		return ServerResponse.createByErrorMessage("Category doesn't exist.");
        	}
        	Category category = optionalCategory.get();
            category.setName(categoryName);
        	Category savedCategory = categorys.save(category);
        	if(savedCategory==null) {
        		return ServerResponse.createByErrorMessage("Failed to rename the category.");
        	}
        	return ServerResponse.createBySuccess("Successfully renamed the category.");
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
}
