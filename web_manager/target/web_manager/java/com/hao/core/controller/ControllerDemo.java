package com.hao.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hao.core.pojo.good.Brand;
import com.hao.core.service.ServiceInterface;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControllerDemo {
    @Reference
    ServiceInterface serviceInterface;
    @RequestMapping("/getname")
    public List<Brand> getName(){

        return serviceInterface.getName();
    }
}
