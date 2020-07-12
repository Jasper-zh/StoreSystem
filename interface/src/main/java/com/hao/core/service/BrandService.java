package com.hao.core.service;

import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.good.Brand;
import java.util.List;
import java.util.Map;

public interface BrandService {
    List<Brand> getAllBrand();
    PageResult getPage(Brand brand, Integer page, Integer pageSize);
    void add(Brand brand);
    Brand getBrandById(Long id);
    void update(Brand brand);
    void delete(Long[] selectList);
    List<Map> selectOptionList();
}
