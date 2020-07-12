package com.hao.core.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.entity.Result;
import com.hao.core.pojo.seller.Seller;
import com.hao.core.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    SellerService sellerService;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @RequestMapping("/add")
    public Result add(@RequestBody Seller seller){
        //获取明文密码
        String password = seller.getPassword();
        //对明文密码进行加密
        String securityPassword = passwordEncoder.encode(password);
        //把加密后的密码存储到seller对象中
        seller.setPassword(securityPassword);
        try{
            sellerService.add(seller);
            return new Result(true,"注册发送成功");
        }catch (Exception e){
            return new Result(false,"注册发送失败");
        }
    }
}
