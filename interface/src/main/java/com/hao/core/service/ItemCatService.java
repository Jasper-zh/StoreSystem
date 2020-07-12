package com.hao.core.service;

import com.hao.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    public List<ItemCat> findByParentId(Long parentId);
    public ItemCat findOne(Long id);

    List<ItemCat> findAll();
}
