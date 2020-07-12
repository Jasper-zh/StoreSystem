package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hao.core.dao.specification.SpecificationDao;
import com.hao.core.dao.specification.SpecificationOptionDao;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.entity.SpecEntity;
import com.hao.core.pojo.specification.Specification;
import com.hao.core.pojo.specification.SpecificationOption;
import com.hao.core.pojo.specification.SpecificationOptionQuery;
import com.hao.core.pojo.specification.SpecificationQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecServiceImpl implements SpecService {
    @Autowired
    SpecificationDao specificationDao;
    @Autowired
    SpecificationOptionDao specificationOptionDao;
    @Override
    public PageResult getPage(Specification specification, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        SpecificationQuery specificationQuery = new SpecificationQuery();
        specificationQuery.setOrderByClause("id desc");
        if(specification!=null) {
            SpecificationQuery.Criteria criteria1 = specificationQuery.createCriteria();
            String name = specification.getSpecName();
            if(name!=null&&!"".equals(name)){
                criteria1.andSpecNameLike("%"+name+"%");
            }
        }
        Page<Specification> specificationList = (Page<Specification>) specificationDao.selectByExample(specificationQuery);
        return new PageResult(specificationList.getTotal(),specificationList.getResult());
    }

    @Override
    public void specSave(SpecEntity specEntity) {
        //1.拿到specification与option
        Specification spec = specEntity.getSpec();
        List<SpecificationOption> options = specEntity.getSpecOption();
        //2.保存spec
        specificationDao.insertSelective(spec);
        //3.添加外键属性再保存
        for (SpecificationOption option : options) {
            option.setSpecId(spec.getId());
            specificationOptionDao.insertSelective(option);
        }

    }

    @Override
    public SpecEntity getSpecEntityById(Long id) {
        //1.查询spec
        Specification spec = specificationDao.selectByPrimaryKey(id);
        //2.外键查询--添加查询条件
        SpecificationOptionQuery optionQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = optionQuery.createCriteria();
        criteria.andSpecIdEqualTo(id);
        //3.查询options
        List<SpecificationOption> options = specificationOptionDao.selectByExample(optionQuery);
        //封装成SpecEntity
        SpecEntity specEntity = new SpecEntity();
        specEntity.setSpec(spec);
        specEntity.setSpecOption(options);
        return specEntity;
    }

    @Override
    public void specUpdate(SpecEntity specEntity) {
        //1.更新spec
        specificationDao.updateByPrimaryKeySelective(specEntity.getSpec());
        //2.打破spec-options关系
        SpecificationOptionQuery optionQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = optionQuery.createCriteria();
        criteria.andSpecIdEqualTo(specEntity.getSpec().getId());
        specificationOptionDao.deleteByExample(optionQuery);
        //3.添加新关系
        for (SpecificationOption option : specEntity.getSpecOption()) {
            option.setSpecId(specEntity.getSpec().getId());
            specificationOptionDao.insertSelective(option);
        }
    }

    @Override
    public void specDelete(Long[] selectList) {

        if(selectList!=null){
            for (Long select : selectList) {
                //1.删除spec
                specificationDao.deleteByPrimaryKey(select);
                //2.删除options
                SpecificationOptionQuery optionQuery = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = optionQuery.createCriteria();
                criteria.andSpecIdEqualTo(select);
                specificationOptionDao.deleteByExample(optionQuery);
            }
        }
    }

    @Override
    public List<Map> selectOptionList() {

        return specificationDao.selectOptionList();
    }
}
