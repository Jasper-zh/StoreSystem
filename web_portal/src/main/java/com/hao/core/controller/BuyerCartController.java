package com.hao.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.hao.core.pojo.entity.BuyerCart;
import com.hao.core.pojo.entity.Result;
import com.hao.core.service.CartService;
import com.hao.core.utils.Constants;
import com.hao.core.utils.CookieUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class BuyerCartController {
    @Reference
    CartService cartService;
    /**
     * 添加商品到购物车
     * @CrossOrigin注解相当于设置了响应头信息, 是w3c支持的一种跨域解决方案
     * origins属性设置的地址是, 返回响应给静态页面, 静态页面所在服务器地址,
     * 也即是service_page项目地址
     * @param itemId    商品库存id
     * @param num       购买数量
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins="http://localhost:8086",allowCredentials="true")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        try {
            //1.获取当前用户名
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //2.获取购物车列表
            List<BuyerCart> cartList = findCartList(request,response);
            //3.将当前商品加入购物车列表
            cartList = cartService.addItemToCartList(cartList,itemId,num);
            //4.存cookie还是redis0
            if("anonymousUser".equals(name)){
                CookieUtil.setCookie(request,response,Constants.CART_LIST_COOKIE,JSON.toJSONString(cartList), 60 * 60 * 24 * 30, "utf-8");
            }else {
                cartService.setCartListToRedis(name,cartList);
            }
            return new Result(true, "添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败!");
        }
    }
    @RequestMapping("/findCartList")
    public List<BuyerCart> findCartList(HttpServletRequest request, HttpServletResponse response) {
        //1. 获取当前登录用户名称
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //2. 从cookie中获取购物车列表json格式字符串
        String cookieCartListStr = CookieUtil.getCookieValue(
                request,
                Constants.CART_LIST_COOKIE,
                "utf-8");
        //3. 如果购物车列表json串为空则返回"[]"
        if (cookieCartListStr == null || "".equals(cookieCartListStr)) {
            cookieCartListStr = "[]";
        }
        //4. 将购物车列表json转换为对象
        List<BuyerCart> cookieCartList = JSON.parseArray(cookieCartListStr, BuyerCart.class);
        //5. 判断用户是否登录, 未登录用户为"anonymousUser"
        if ("anonymousUser".equals(userName)) {
            //5.1. 未登录, 返回cookie中的购物车列表对象
            System.out.println("返回cookie购物车对象列表:"+cookieCartList);
            return cookieCartList;
        } else {
            //5.2.1.已登录, 从redis中获取购物车列表对象
            List<BuyerCart> redisCartList = cartService.getCartListFromRedis(userName);
            //5.2.2.判断cookie中是否存在购物车列表
            if (cookieCartList.size() > 0) {
                //如果cookie中存在购物车列表则和redis中的购物车列表合并成一个对象
                redisCartList = cartService.mergeCookieCartListToRedisCartList(
                        cookieCartList,
                        redisCartList);
                //删除cookie中购物车列表
                CookieUtil.deleteCookie(request, response, Constants.CART_LIST_COOKIE);
                //将合并后的购物车列表存入redis中
                cartService.setCartListToRedis(userName, redisCartList);
            }
            //5.2.3.返回购物车列表对象
            System.out.println("返回redis购物车对象列表:"+redisCartList);
            return redisCartList;
        }
    }
}
