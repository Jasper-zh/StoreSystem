package com.hao.core.service;

import com.hao.core.pojo.entity.BuyerCart;

import java.util.List;

public interface CartService {
    public List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num);
    public  void setCartListToRedis(String userName, List<BuyerCart> cartList);

    public List<BuyerCart> getCartListFromRedis(String userName);

    public List<BuyerCart> mergeCookieCartListToRedisCartList(
            List<BuyerCart> cookieCartList,
            List<BuyerCart> redisCartList);
}
