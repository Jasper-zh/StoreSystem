package com.hao.core.service;

import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TempService {
    PageResult getPage(Integer page, Integer pageSize, TypeTemplate temp);
    void addTemp(TypeTemplate template);
    void tempDelete(Long[] selectList);
    TypeTemplate findOne(Long id);
    List<Map> findBySpecList(Long id);
}
