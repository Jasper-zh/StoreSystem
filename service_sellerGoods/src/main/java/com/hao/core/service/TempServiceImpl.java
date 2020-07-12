package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hao.core.dao.specification.SpecificationOptionDao;
import com.hao.core.dao.template.TypeTemplateDao;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.specification.Specification;
import com.hao.core.pojo.specification.SpecificationOption;
import com.hao.core.pojo.specification.SpecificationOptionQuery;
import com.hao.core.pojo.specification.SpecificationQuery;
import com.hao.core.pojo.template.TypeTemplate;
import com.hao.core.pojo.template.TypeTemplateQuery;
import com.hao.core.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TempServiceImpl implements TempService{
    @Autowired
    TypeTemplateDao templateDao;
    @Autowired
    SpecificationOptionDao optionDao;
    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public PageResult getPage(Integer page, Integer pageSize, TypeTemplate temp) {

            //redis中缓存模板所有数据
            List<TypeTemplate> templateAll = templateDao.selectByExample(null);
            for (TypeTemplate typeTemplate : templateAll) {
                //模板id作为key, 品牌集合作为value缓存入redis中
                String brandIdsJsonStr = typeTemplate.getBrandIds();
                //将json转换成集合
                List<Map> brandList = JSON.parseArray(brandIdsJsonStr, Map.class);
                redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS)
                        .put(typeTemplate.getId(), brandList);

                //模板id作为key, 规格集合作为value缓存入redis中
                List<Map> specList = findBySpecList(typeTemplate.getId());
                redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS)
                        .put(typeTemplate.getId(), specList);
            }
            System.out.println("从mysql更新模板数据redis");


        //分页查询
        PageHelper.startPage(page,pageSize);
        TypeTemplateQuery templateQuery = new TypeTemplateQuery();
        templateQuery.setOrderByClause("id desc");
        if(temp!=null) {
            TypeTemplateQuery.Criteria criteria = templateQuery.createCriteria();
            String name = temp.getName();
            if(name!=null&&!"".equals(name)){
                criteria.andNameLike("%"+name+"%");
            }
        }
        Page<TypeTemplate> templates = (Page<TypeTemplate>) templateDao.selectByExample(templateQuery);
        return new PageResult(templates.getTotal(),templates.getResult());
    }
    @Override
    public void addTemp(TypeTemplate template) {
        templateDao.insertSelective(template);
    }

    @Override
    public void tempDelete(Long[] selectList) {
        System.out.println(selectList);
        if(selectList!=null){
            for (Long select : selectList) {
                //1.删除spec
                templateDao.deleteByPrimaryKey(select);

            }
        }
    }

    @Override
    public TypeTemplate findOne(Long id) {
        System.out.println(id);
        return templateDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        //1. 根据模板id查询模板对象
        TypeTemplate typeTemplate = templateDao.selectByPrimaryKey(id);
        //2. 从模板对象中获取规格集合数据, 获取到的是json格式字符串
        //数据格式例如: [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        //3. 将json格式字符串解析成Java中的List集合对象
        List<Map> maps = JSON.parseArray(specIds, Map.class);

        //4. 遍历集合对象
        if (maps != null) {
            for (Map map : maps) {
                //5. 遍历过程中根据规格id, 查询对应的规格选项集合数据
                Long specId = Long.parseLong(String.valueOf(map.get("id")));
                //6. 将规格选项再封装到规格数据中一起返回
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(specId);
                //根据规格id获取规格选项集合数据
                List<SpecificationOption> optionList =  optionDao.selectByExample(query);
                //将规格选项集合数据封装到原来的map中
                map.put("options", optionList);
            }
        }
        return maps;
    }
}
