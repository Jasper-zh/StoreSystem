package com.hao.core.listener;

import com.hao.core.service.CmsService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Map;

public class PageListener implements MessageListener {
    @Autowired
    private CmsService cmsService;
    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
        try {
            String goodsId = atm.getText();
            cmsService.createStaticPage(Long.parseLong(goodsId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
