package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hao.core.pojo.item.Item;
import com.hao.core.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map paramMap) {

        System.out.println("paramMap:"+paramMap);

        //1.高亮查询
        Map<String, Object> resultMap = highlightSearch(paramMap);


        //2. 根据查询参数, 到solr中获取对应的分类结果集, 由于分类重复, 所以需要分组去重
        List<String> groupCatagoryList = findGroupCatagoryList(paramMap);
        resultMap.put("categoryList", groupCatagoryList);


        //3. 判断paramMap传入参数中是否有分类名称
        String category = String.valueOf(paramMap.get("category"));
        if (category != null && !"".equals(category)) {
            //4. 如果有分类参数, 则根据分类查询对应的品牌集合和规格集合
            Map specListAndBrandList = findSpecListAndBrandList(category);
            resultMap.putAll(specListAndBrandList);
        } else {
            //5.若果没有默认根据第一个分类查询对应的品牌集合和规格集合
            Map specListAndBrandList = findSpecListAndBrandList(groupCatagoryList.get(0));
            System.out.println("specListAndBrandList:"+specListAndBrandList);
            resultMap.putAll(specListAndBrandList);
        }
        System.out.println("resultMap:"+resultMap);

        return resultMap;
    }



    /**
     * 根据关键字到到solr去查询标题以高亮方式标记关键字
     * @param paramMap 页面传人的查询参数
     * @return
     */
    private Map<String,Object> highlightSearch(Map paramMap){
        //获取页面点击的分类过滤条件
        String category = String.valueOf(paramMap.get("category"));
        //获取页面点击的品牌过滤条件
        String brand = String.valueOf(paramMap.get("brand"));
        //获取页面点击的规格过滤条件
        String spec = String.valueOf(paramMap.get("spec"));

        //获取查询条件
        String keywords = String.valueOf(paramMap.get("keywords"));
        System.out.println("keywords:"+keywords);
        //当前页
        Integer pageNo = Integer.parseInt(String.valueOf(paramMap.get("pageNo")));
        //每页查询多少条数据
        Integer pageSize = Integer.parseInt(String.valueOf(paramMap.get("pageSize")));
//-------------------------------------------------------------
        //创建高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //条件查询
        //根据分类过滤查询
        if (category != null && !"".equals(category)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建条件对象
            Criteria filterCriteria = new Criteria("item_category").is(category);
            //将条件对象放入过滤对象中
            filterQuery.addCriteria(filterCriteria);
            //过滤对象放入查询对象中
            query.addFilterQuery(filterQuery);
        }
        //根据品牌过滤查询
        if (brand != null && !"".equals(brand)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建条件对象
            Criteria filterCriteria = new Criteria("item_brand").is(brand);
            //将条件对象放入过滤对象中
            filterQuery.addCriteria(filterCriteria);
            //过滤对象放入查询对象中
            query.addFilterQuery(filterQuery);
        }
        //根据规格过滤查询 spec中的数据格式{网络:移动4G, 内存小大: 16G}
        if (spec != null && !"".equals(spec)) {
            Map<String, String> speMap = JSON.parseObject(spec, Map.class);
            if (speMap != null && speMap.size() > 0) {
                Set<Map.Entry<String, String>> entries = speMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    //创建条件对象
                    Criteria filterCriteria = new Criteria("item_spec_" + entry.getKey())
                            .is(entry.getValue());
                    //将条件对象放入过滤对象中
                    filterQuery.addCriteria(filterCriteria);
                    //过滤对象放入查询对象中
                    query.addFilterQuery(filterQuery);
                }
            }
        }
        //创建高亮选项对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置哪个域需要高亮显示
        highlightOptions.addField("item_title");
        //设置高亮前缀
        highlightOptions.setSimplePrefix("<em style=\"color:red\">");
        //设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //将高亮选项加入到查询对象中
        query.setHighlightOptions(highlightOptions);

        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入到查询对象当中
        query.addCriteria(criteria);
//----------------------------------------------------------------

        //计算从第几条开始查
        if (pageNo == null || pageNo <= 0){
            pageNo = 1;
        }

        Integer start = (pageNo - 1) * pageSize;
        //设置从第几条开始查询
        query.setOffset(start);
        //设置每页查询多少条
        query.setRows(pageSize);

        //查询,并返回结果
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();

        //定义存在高亮item集合
        List<Item> itemList = new ArrayList<>();
        //遍历高亮集合
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //获取到不带高亮的实体对象
            Item item = itemHighlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if (highlights != null && highlights.size() > 0) {
                //获取到高亮标题集合
                List<String> highlightTitle = highlights.get(0).getSnipplets();
                if (highlightTitle != null && highlightTitle.size() > 0) {
                    //终于获取到高亮的标题
                    String title = highlightTitle.get(0);
                    //使用高亮的内容替换不带高亮的标题
                    item.setTitle(title);
                }
            }
            itemList.add(item);
        }


        Map<String, Object> resultMap = new HashMap<>();
        //查询到的结果集
        resultMap.put("rows",  items.getContent());
        //查询到的总页数
        resultMap.put("totalPages", items.getTotalPages());
        //查询到的总条数
        resultMap.put("total", items.getTotalElements());
        return resultMap;
    }
    /**
     * 根据关键字到solr中查询结果集中对应的分类集合, 要分组去重
     * @param paramMap  页面传入的查询参数
     * @return
     */
    private List<String> findGroupCatagoryList(Map paramMap) {
        System.out.println(paramMap);
        List<String> resultList = new ArrayList<>();
        //获取查询关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        //创建查询对象
        Query query = new SimpleQuery();
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入查询对象中
        query.addCriteria(criteria);
        //创建分组对象
        GroupOptions groupOptions = new GroupOptions();
        //设置根据分类域进行分组
        groupOptions.addGroupByField("item_category");
        //将分组对象放入查询对象中
        query.setGroupOptions(groupOptions);

        //分组查询分类集合
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        //获取结果集中分类域的集合
        GroupResult<Item> item_category = items.getGroupResult("item_category");
        //获取分类域的实体集合
        Page<GroupEntry<Item>> groupEntries = item_category.getGroupEntries();
        System.out.println(groupEntries);
        //遍历实体集合得到实体对象
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String groupCategory = groupEntry.getGroupValue();
            resultList.add(groupCategory);
        }

        return resultList;
    }

    private Map findSpecListAndBrandList(String categoryName) {
        //1. 根据分类名称到redis中查询对应的模板id
        Long templateId = (Long)redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS)
                .get(categoryName);
        System.out.println("templateId:"+templateId);
        if(templateId!=null) {
            //2. 根据模板id到redis中查询对应的品牌集合
            List<Map> brandList = (List<Map>)redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS)
                    .get(templateId);
            //3. 根据模板id到reids中查询对应的规格集合
            List<Map> specList = (List<Map>)redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS)
                    .get(templateId);
            //4. 将品牌集合和规格集合数据封装到Map中返回
            Map resultMap = new HashMap();
            resultMap.put("brandList", brandList);
            resultMap.put("specList", specList);
            return resultMap;
        }

        return null;
    }
}
