package com.hao.core.listener;

import com.hao.core.service.SolrManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;

public class ItemDeleteListener implements MessageListener {
    @Autowired
    private SolrManagerService solrManagerService;
    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
        try {
            String goodsId = atm.getText();
            ArrayList<Object> itemGoodsID = new ArrayList<>();
            itemGoodsID.add(Long.parseLong(goodsId));
            solrManagerService.deleteItemByGoodsId(itemGoodsID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}