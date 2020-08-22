package com.hao.core.service;

import com.hao.core.pojo.entity.GoodsEntity;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.good.Goods;
import com.hao.core.pojo.item.Item;

import java.util.List;

public interface GoodsService {
    void add(GoodsEntity goodsEntity);
    PageResult findPage(Goods goods, Integer page, Integer rows);
    GoodsEntity findOne(Long id);

    void update(GoodsEntity goodsEntity);

    void delete(Long[] ids);

    void updateStatus(Long[] ids, String status);

    public List<Item> findItemByGoodsIdAndStatus(Long[]ids, String status);
}
