package com.hao.core.pojo.entity;


import com.hao.core.pojo.good.Goods;
import com.hao.core.pojo.good.GoodsDesc;
import com.hao.core.pojo.item.Item;

import java.io.Serializable;
import java.util.List;

public class GoodsEntity implements Serializable {

    private Goods goods;
    private GoodsDesc goodsDesc;
    List<Item> itemList;

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public void setGoodsDesc(GoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }



    public Goods getGoods() {
        return goods;
    }

    public GoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    @Override
    public String toString() {
        return "GoodsEntity{" +
                "goods=" + goods +
                ", goodsDesc=" + goodsDesc +
                ", itemList=" + itemList +
                '}';
    }


}
