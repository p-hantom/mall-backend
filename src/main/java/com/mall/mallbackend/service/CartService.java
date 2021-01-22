package com.mall.mallbackend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;

import com.mall.mallbackend.common.Const;
import com.mall.mallbackend.model.Cart;
import com.mall.mallbackend.model.Product;
import com.mall.mallbackend.repository.CartRepository;
import com.mall.mallbackend.repository.ProductRepository;
import com.mall.mallbackend.util.BigDecimalUtil;
import com.mall.mallbackend.util.PropertiesUtil;
import com.mall.mallbackend.vo.CartProductVo;
import com.mall.mallbackend.vo.CartVo;
@Controller
public class CartService {
	private final CartRepository carts;
	private final ProductRepository products;
	
	public CartService(CartRepository carts,ProductRepository products) {
		this.carts = carts;
		this.products = products;
	}
	
	public CartVo getCartVo(Integer userId) {
		CartVo cartVo = new CartVo();
        List<Cart> cartList = carts.findByUserId(userId);
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(cartList!=null && cartList.size()>0){
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Optional<Product> optionalProduct = products.findById(cartItem.getProductId());
                Product product = optionalProduct.get();
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存 ???
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        carts.save(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("file.prefix"));

        return cartVo;
	}

	public Boolean getAllCheckedStatus(Integer userId) {
		if(userId == null) {
			return false;
		}
		return carts.selectCartProductCheckedStatusByUserId(userId)==0;
	}
}
