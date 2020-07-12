package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hao.core.dao.ad.ContentCategoryDao;
import com.hao.core.pojo.ad.ContentCategory;
import com.hao.core.pojo.ad.ContentCategoryQuery;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private ContentCategoryDao categoryDao;

    @Override
    public PageResult findPage(ContentCategory category, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        ContentCategoryQuery query = new ContentCategoryQuery();
        ContentCategoryQuery.Criteria criteria = query.createCriteria();
        if (category != null) {
            if (category.getName() != null && !"".equals(category.getName())) {
                criteria.andNameLike("%" + category.getName() + "%");
            }
        }
        Page<ContentCategory> categoryList = (Page<ContentCategory>) categoryDao.selectByExample(query);
        return new PageResult(categoryList.getTotal(), categoryList.getResult());
    }
    @Override
    public void add(ContentCategory category) {
        categoryDao.insertSelective(category);
    }
    @Override
    public void delete(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                categoryDao.deleteByPrimaryKey(id);
            }
        }
    }
    @Override
    public ContentCategory findOne(Long id) {
        return categoryDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(ContentCategory category) {

        categoryDao.updateByPrimaryKeySelective(category);
    }

    @Override
    public List<ContentCategory> findAll() {
        return categoryDao.selectByExample(null);
    }


}
