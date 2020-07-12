package com.hao.core.service;

import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.entity.SpecEntity;
import com.hao.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecService {
    PageResult getPage(Specification specification, Integer page, Integer pageSize);
    void specSave(SpecEntity specEntity);
    SpecEntity getSpecEntityById(Long id);
    void specUpdate(SpecEntity specEntity);
    void specDelete(Long[] selectList);
    List<Map> selectOptionList();
}
