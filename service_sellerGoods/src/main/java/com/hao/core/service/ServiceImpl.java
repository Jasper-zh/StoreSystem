package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.hao.core.dao.good.BrandDao;
import com.hao.core.pojo.good.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiceImpl implements ServiceInterface {
    @Autowired
    private BrandDao brandDao;
    @Override
    public List<Brand> getName() {
        List<Brand> brands = brandDao.selectByExample(null);
        return brands;
    }
}
