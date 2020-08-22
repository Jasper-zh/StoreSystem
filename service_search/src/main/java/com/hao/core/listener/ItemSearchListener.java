package com.hao.core.listener;

import com.alibaba.fastjson.JSON;
import com.hao.core.dao.item.ItemDao;
import com.hao.core.pojo.item.Item;
import com.hao.core.pojo.item.ItemQuery;
import com.hao.core.service.SolrManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;
import java.util.Map;

public class ItemSearchListener implements MessageListener {
    @Autowired
    private SolrManagerService solrManagerService;
    @Autowired
    private ItemDao itemDao;
    @Override
    public void onMessage(Message message) {
        //为了方便获取文本消息, 将原生的消息对象转换成activeMq的文本消息对象
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
        try {
            String goodsId = atm.getText();
            ItemQuery query = new ItemQuery();
            ItemQuery.Criteria criteria = query.createCriteria();
            //查询指定商品的库存数据
            criteria.andGoodsIdEqualTo(Long.parseLong(goodsId));
            List<Item> items = itemDao.selectByExample(query);
            if (items != null) {
                for (Item item : items) {
                    //获取规格json格式字符串
                    String specJsonStr = item.getSpec();
                    Map map = JSON.parseObject(specJsonStr, Map.class);
                    item.setSpecMap(map);
                }
            }
            solrManagerService.saveItemToSolr(items);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}