package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.hao.core.dao.item.ItemCatDao;
import com.hao.core.pojo.item.ItemCat;
import com.hao.core.pojo.item.ItemCatQuery;
import com.hao.core.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private ItemCatDao catDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<ItemCat> findByParentId(Long parentId) {

            //获取所有分类数据
            List<ItemCat> itemCatAll = catDao.selectByExample(null);
            //分类名称作为key, typeId也就是模板id作为value, 缓存到redis当中
            for (ItemCat itemCat : itemCatAll) {
                redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS)
                        .put(itemCat.getName(), itemCat.getTypeId());
            }
        redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).get("电子书");
        System.out.println("从mysql更新redis");

        //根据父级id查询它的子集, 展示到页面
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<ItemCat> itemCats = catDao.selectByExample(query);
        return itemCats;
    }
    @Override
    public ItemCat findOne(Long id) {
        return catDao.selectByPrimaryKey(id);
    }
    @Override
    public List<ItemCat> findAll() {
        return catDao.selectByExample(null);
    }

}
