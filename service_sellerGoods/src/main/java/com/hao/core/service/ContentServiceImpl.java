package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hao.core.dao.ad.ContentDao;
import com.hao.core.pojo.ad.Content;
import com.hao.core.pojo.ad.ContentQuery;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentDao contentDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public PageResult findPage(Content content, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        if (content != null) {
            if (content.getTitle() != null && !"".equals(content.getTitle())) {
                criteria.andTitleLike("%"+content.getTitle()+"%");
            }
        }
        Page<Content> contentList = (Page<Content>)contentDao.selectByExample(query);
        return new PageResult(contentList.getTotal(), contentList.getResult());
    }
    @Override
    public void add(Content content) {
        //1. 将新广告添加到数据库中
        contentDao.insertSelective(content);
        //2. 根据分类id, 到redis中删除对应分类的广告集合数据
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
    }

    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Content content) {
        //1. 根据广告id, 到数据库中查询原来的广告对象
        Content oldContent = contentDao.selectByPrimaryKey(content.getId());
        //2. 根据原来的广告对象中的分类id, 到redis中删除对应的广告集合数据
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(oldContent.getCategoryId());
        //3. 根据传入的最新的广告对象中的分类id, 删除redis中对应的广告集合数据
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
        //4. 将新的广告对象更新到数据库中
        contentDao.updateByPrimaryKeySelective(content);
    }
    @Override
    public void delete(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                //1. 根据广告id, 到数据库中查询广告对象
                Content content = contentDao.selectByPrimaryKey(id);
                //2. 根据广告对象中的分类id, 删除redis中对应的广告集合数据
                redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
                //3. 根据广告id删除数据库中的广告数据
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }
    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<Content> list = contentDao.selectByExample(query);
        return list;
    }
    @Override
    public List<Content> findByCategoryIdFromRedis(Long categoryId) {
        //1. 首先根据分类id到redis中获取数据
        List<Content> contentList = (List<Content>)redisTemplate
                .boundHashOps(Constants.CONTENT_LIST_REDIS)
                .get(categoryId);
        //2. 如果redis中没有数据则到数据库中获取数据
        if (contentList == null) {
            //3. 如果数据库中获取到数据, 则放入redis中一份
            contentList = findByCategoryId(categoryId);
            redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).put(categoryId, contentList);
            System.out.println("从mysql获取数据");
        }
        System.out.println("从redis获取的数据");
        return contentList;
    }
}

