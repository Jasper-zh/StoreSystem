package com.hao.core.service;

import com.hao.core.pojo.ad.ContentCategory;
import com.hao.core.pojo.entity.PageResult;

import java.util.List;

public interface CategoryService {
    PageResult findPage(ContentCategory category, Integer page, Integer rows);

    void add(ContentCategory category);

    void delete(Long[] ids);

    ContentCategory findOne(Long id);

    void update(ContentCategory category);

    List<ContentCategory> findAll();
}
