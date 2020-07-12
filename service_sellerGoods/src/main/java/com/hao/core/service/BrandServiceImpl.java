package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hao.core.dao.good.BrandDao;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.good.Brand;
import com.hao.core.pojo.good.BrandQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    BrandDao brandDao;

    @Override
    public List<Brand> getAllBrand() {
        return brandDao.selectByExample(null);
    }

    @Override
    public PageResult getPage(Brand brand,Integer page,Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        BrandQuery brandQuery = new BrandQuery();
        brandQuery.setOrderByClause("id desc");
        if(brand!=null){
            String name = brand.getName();
            String firstChar = brand.getFirstChar();
            BrandQuery.Criteria criteria = brandQuery.createCriteria();
            if(name!=null&&!"".equals(name)){
                criteria.andNameLike("%"+name+"%");
            }
            if(firstChar!=null&&!"".equals(firstChar)){
                criteria.andFirstCharLike("%"+firstChar+"%");
            }
        }
        Page<Brand> brandList = (Page<Brand>) brandDao.selectByExample(brandQuery);
        return new PageResult(brandList.getTotal(),brandList.getResult());
    }

    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    @Override
    public Brand getBrandById(Long id) {

        return brandDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Long[] selectList) {
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        criteria.andIdIn(Arrays.asList(selectList));
        brandDao.deleteByExample(brandQuery);
    }

    @Override
    public List<Map> selectOptionList() {
       return brandDao.selectOptionList();
    }
}
