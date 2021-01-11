package com.mall.mallbackend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import com.mall.mallbackend.common.PageInfo;
import com.mall.mallbackend.common.ServerResponse;
import com.mall.mallbackend.model.Product;
import com.mall.mallbackend.vo.ProductDetailVo;
import com.mall.mallbackend.vo.ProductListVo;
@Controller
public class ProductService {
	public ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
//        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }
	
	public ProductDetailVo assembleProductDetailVo(Product product){
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
	
	public ServerResponse<PageInfo> assembleProductPageInfo(Page<Product> pagedResult){
		PageInfo<Product, ProductListVo> pageInfo;
		List<ProductListVo> productListVoList = new ArrayList<>();
    	if(pagedResult.hasContent()) {
    		// Assemble as ProductListVo list
			for(Product product: pagedResult.getContent()) {
				ProductListVo productListVo = this.assembleProductListVo(product);
	            productListVoList.add(productListVo);
			}
			// Assemble as PageInfo
			pageInfo = new PageInfo(pagedResult, productListVoList, "");
			return ServerResponse.createBySuccess(pageInfo);
    	} else {
        	System.out.println("empty list");
        	return ServerResponse.createBySuccess(new PageInfo<Product, ProductListVo>());
        }
	}
}
