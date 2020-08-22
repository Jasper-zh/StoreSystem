package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import java.util.List;

@Service
public class SolrManagerServiceImpl implements SolrManagerService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public void saveItemToSolr(List list) {
        if (list !=null){
            solrTemplate.saveBeans(list);
            solrTemplate.commit();
        }
    }

    @Override
    public void deleteItemByGoodsId(List goodsId) {
        if(goodsId!=null){
            Query query = new SimpleQuery();
            Criteria criteria = new Criteria("item_goodsId").in(goodsId);
            query.addCriteria(criteria);
            solrTemplate.delete(query);
            solrTemplate.commit();
        }
    }
}
