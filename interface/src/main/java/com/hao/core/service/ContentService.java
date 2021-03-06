package com.hao.core.service;

import com.hao.core.pojo.ad.Content;
import com.hao.core.pojo.entity.PageResult;

import java.util.List;

public interface ContentService {
    PageResult findPage(Content content, Integer page, Integer rows);
    void add(Content content);

    Content findOne(Long id);

    void update(Content content);

    void delete(Long[] ids);

    public List<Content> findByCategoryId(Long categoryId);

    public List<Content> findByCategoryIdFromRedis(Long categoryId);
}
