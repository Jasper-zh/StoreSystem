package com.hao.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hao.core.pojo.item.ItemCat;
import com.hao.core.service.test;
import com.sun.corba.se.impl.orb.ParserTable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {
    @Reference
    private test test;

    @RequestMapping("/test")
    public String findByParentId() {
        return test.test();
    }
}
