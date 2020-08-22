package com.hao.core.service;

import com.hao.core.pojo.log.PayLog;
import com.hao.core.pojo.order.Order;

public interface OrderService {
    public void add(Order order);

    PayLog getPayLogByUserName(String userName);

    void updatePayStatus(String userName);
}