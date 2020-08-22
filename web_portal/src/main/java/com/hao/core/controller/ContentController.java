package com.hao.core.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.hao.core.pojo.ad.Content;
import com.hao.core.service.ContentService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    ContentService contentService;
    @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId){
        return contentService.findByCategoryIdFromRedis(categoryId);
    }

}
