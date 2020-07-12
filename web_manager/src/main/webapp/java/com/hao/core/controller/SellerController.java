package com.hao.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.entity.Result;
import com.hao.core.pojo.seller.Seller;
import com.hao.core.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    SellerService sellerService;
    @RequestMapping("/getPage")
    public PageResult getPage(Integer page, Integer pageSize, @RequestBody Seller seller){
        return sellerService.getPage(page,pageSize,seller);
    }
    @RequestMapping("/updateStatus")
    public Result updateStatus(@RequestBody Seller seller){
        return sellerService.updateStatus(seller);
    }
}
