package com.hao.core.service;

import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.entity.Result;
import com.hao.core.pojo.seller.Seller;

public interface SellerService {
    void add(Seller seller);
    PageResult getPage(Integer page, Integer pageSize, Seller seller);
    Result updateStatus(Seller seller);
    Seller getSeller(String username);
}
