package com.mall.mallbackend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
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
import com.mall.mallbackend.model.Product;
import com.mall.mallbackend.model.User;
import com.mall.mallbackend.repository.ProductRepository;
import com.mall.mallbackend.service.ProductService;
import com.mall.mallbackend.service.UserService;
import com.mall.mallbackend.vo.ProductDetailVo;
import com.mall.mallbackend.vo.ProductListVo;

@RestController
@RequestMapping(path="/manage/product")
public class ProductManageController {
	private final ProductRepository products;
	private final UserService userService;
	private final ProductService productService;
	public ProductManageController(ProductRepository products, UserService userService, ProductService productService) {
		this.products = products;
		this.userService = userService;
		this.productService = productService;
	}
	
	@PostMapping("list.do")
    public ServerResponse<PageInfo> list(
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
        	Page<Product> pagedResult = products.findAll(paging);
        	
        	return productService.assembleProductPageInfo(pagedResult);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
	
	@PostMapping("search.do")
    public ServerResponse<PageInfo> productSearch(
    		HttpSession session,
    		@RequestParam(value = "productName",required=false) String productName,
    		@RequestParam(value = "productId",required=false) Integer productId,
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
        	Page<Product> pagedResult = products.findByNameContainingAndId(productName, productId, paging);
        	
        	return productService.assembleProductPageInfo(pagedResult);
//        	// Wrap selected data
//    		List<ProductListVo> productListVoList = new ArrayList<>();
//        	if(pagedResult.hasContent()) {
//        		// Assemble as ProductListVo list
//    			for(Product product: pagedResult.getContent()) {
//    				ProductListVo productListVo = productService.assembleProductListVo(product);
//    	            productListVoList.add(productListVo);
//    			}
//    			// Assemble as PageInfo
//    			pageInfo = new PageInfo(pagedResult, productListVoList, "");
//    			return ServerResponse.createBySuccess(pageInfo);
//        	} else {
//            	System.out.println("empty list");
//            	return ServerResponse.createBySuccess(new PageInfo<Product, ProductListVo>());
//            }
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
	
	@PostMapping("detail.do")
    public ServerResponse<ProductDetailVo> detail(
    		HttpSession session,
    		@RequestParam(value = "productId") Integer productId,
    		@RequestParam(value = "pageNum",defaultValue = "0") int pageNum,
	        @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充业务
        	if(productId == null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            Optional<Product> optionalProduct = products.findById(productId);
            if(optionalProduct.isEmpty()) {
            	return ServerResponse.createByErrorMessage("产品已下架或者删除");
            }
            ProductDetailVo productDetailVo = productService.assembleProductDetailVo(optionalProduct.get());
        	return ServerResponse.createBySuccess(productDetailVo);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
	
	@PostMapping("set_sale_status.do")
	public ServerResponse<String> setSaleStatus(HttpSession session,
    		@RequestParam(value = "productId") Integer productId,
    		@RequestParam(value = "status") Integer status) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充业务
        	if(productId == null || status == null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            Optional<Product> optionalProduct = products.findById(productId);
            if(optionalProduct.isEmpty()) {
            	return ServerResponse.createByErrorMessage("产品已下架或者删除");
            }
            Product product = optionalProduct.get();
            product.setStatus(status);
            Product savedProduct = products.save(product);
            if(savedProduct!=null) {
            	return ServerResponse.createBySuccess("修改产品销售状态成功");
            }
        	return ServerResponse.createByErrorMessage("修改产品销售状态失败");
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
	
	@PostMapping("save.do")
	public ServerResponse<String> saveOrUpdataProduct(HttpSession session,
			Product product
//    		@RequestParam(value = "productId") Integer productId,
//    		@RequestParam(value = "categoryId") Integer categoryId,
//    		@RequestParam(value = "name") String name,
//    		@RequestParam(value = "subtitle") String subtitle,
//    		@RequestParam(value = "mainImage") String mainImage,
//    		@RequestParam(value = "subImages") String subImages,
//    		@RequestParam(value = "detail") String detail,
//    		@RequestParam(value = "status") Integer status
    		) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            //填充业务
        	if(product != null)
            {
                if(StringUtils.isNotBlank(product.getSubImages())){
                    String[] subImageArray = product.getSubImages().split(",");
                    if(subImageArray.length > 0){
                        product.setMainImage(subImageArray[0]);
                    }
                }
                
                Product savedProduct = products.save(product);
                if(savedProduct!=null) {
                	return ServerResponse.createBySuccess("更新产品成功");
                } 
                return ServerResponse.createBySuccess("更新产品失败");
            }
            return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
	}
}
