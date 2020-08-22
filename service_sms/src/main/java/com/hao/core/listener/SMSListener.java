package com.hao.core.listener;

import com.alibaba.dubbo.config.annotation.Service;
import com.hao.core.service.SmsUtil;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

public class SMSListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        MapMessage map = (MapMessage)message;
        try {
            SmsUtil.sendSms( map.getString("mobile"),
                    map.getString("template_code"),
                    map.getString("sign_name"),
                    map.getString("param"));
        } catch (JMSException e) {
            e.printStackTrace();
        }
        System.out.println(map);
    }
}
