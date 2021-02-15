package com.mall.mallbackend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mall.mallbackend.common.Const;
import com.mall.mallbackend.common.PageInfo;
import com.mall.mallbackend.common.ResponseCode;
import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.Category;
import com.mall.mallbackend.model.Product;
import com.mall.mallbackend.repository.CategoryRepository;
import com.mall.mallbackend.repository.ProductRepository;
import com.mall.mallbackend.service.CategoryService;
import com.mall.mallbackend.util.PropertiesUtil;
import com.mall.mallbackend.vo.ProductDetailVo;
import com.mall.mallbackend.vo.ProductListVo;


@RestController
@RequestMapping(path="/product")
public class ProductController {
	private final ProductRepository products;
	private final CategoryService categoryService;
	
	public ProductController(ProductRepository products,CategoryRepository categoryRepository) {
		this.products = products;
		this.categoryService = new CategoryService(categoryRepository);
	}
	
	@PostMapping(path="/detail.do")
	public ServerResponse<ProductDetailVo> getProductDetail(@RequestParam(value="productId") Integer productId) {
		if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
		// Find in database
		Optional<Product> productOptional = this.products.findById(productId);
		
		if(!productOptional.isPresent()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
		
		Product product = productOptional.get();
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServerResponse.createBySuccess(productDetailVo);
	}
	
	@PostMapping("list.do")
    public ServerResponse<PageInfo<Product, ProductListVo> > ProductByKeywordAndCategory(
    		  @RequestParam(value = "keyword",required = false)String keyword,
	          @RequestParam(value = "categoryId",required = false)Integer categoryId,
	          @RequestParam(value = "pageNum",defaultValue = "0") int pageNum,
	          @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
	          @RequestParam(value = "orderBy",defaultValue = "") String orderBy) {
		System.out.println("keyword:"
				+ ""+keyword);
		Pageable paging;
		PageInfo<Product, ProductListVo> pageInfo;
		Page<Product> pagedResult;
		List<Integer> categoryIdList = new ArrayList<>();
		
		// Order by
		if(orderBy.equals("")){
			paging = PageRequest.of(pageNum, pageSize, Sort.unsorted());
        } else {
			paging = PageRequest.of(pageNum, pageSize, Sort.by(orderBy));
		}
		
		// Category
		if(categoryId != null) {
			Optional<Category> category = categoryService.findById(categoryId);
			if(category.get()==null && StringUtils.isBlank(keyword)) {
				return ServerResponse.createBySuccess(new PageInfo<Product, ProductListVo>());
			}
			
			categoryIdList = categoryService.selectCategoryAndChildrenById(categoryId).getData();
		}
		
		// Find in database
		pagedResult = products.findByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList, paging);
		
		// Wrap selected data
		List<ProductListVo> productListVoList = new ArrayList<>();
		if(pagedResult.hasContent()) {
			// Assemble as ProductListVo list
			for(Product product: pagedResult.getContent()) {
				ProductListVo productListVo = assembleProductListVo(product);
	            productListVoList.add(productListVo);
			}
			
			// Assemble as PageInfo
			pageInfo = new PageInfo<Product, ProductListVo>(pagedResult, productListVoList, orderBy);
			
			return ServerResponse.createBySuccess(pageInfo);
        } else {
        	System.out.println("empty list");
        	return ServerResponse.createBySuccess(new PageInfo<Product, ProductListVo>());
        }
    }
	
	private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

//        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
//
//        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
//        if(category == null){
//            productDetailVo.setParentCategoryId(0);//默认根节点
//        }else{
//            productDetailVo.setParentCategoryId(category.getParentId());
//        }
//
//        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
//        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }
	
	private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("file.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }
	
	
}
