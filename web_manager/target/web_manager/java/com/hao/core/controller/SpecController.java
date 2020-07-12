package com.hao.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.entity.Result;
import com.hao.core.pojo.entity.SpecEntity;
import com.hao.core.pojo.specification.Specification;
import com.hao.core.service.SpecService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spec")
public class SpecController {
    @Reference
    SpecService specService;

    /**
     * 加载页面
     * @param spec 查询条件
     * @param page 当前查询页
     * @param pageSize 每页记录数
     * @return
     */
    @RequestMapping("/getPage")
    public PageResult search(@RequestBody Specification spec, Integer page, Integer pageSize) {
        PageResult result = specService.getPage(spec, page, pageSize);
        return result;
    }
    @RequestMapping("/specSave")
    public Result specSave(@RequestBody SpecEntity specEntity){

        System.out.println("===============Controller=============");
        System.out.println(specEntity);
        try{
            specService.specSave(specEntity);
            return new Result(true,"保存成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }
    @RequestMapping("/getSpecEntityById")
    public SpecEntity getSpecEntityById(Long id){
        System.out.println("=============="+id+"==============");
        System.out.println(specService.getSpecEntityById(id));
        return specService.getSpecEntityById(id);
    }
    @RequestMapping("/specUpdate")
    public Result specUpdate(@RequestBody SpecEntity specEntity){
        try {
            specService.specUpdate(specEntity);
            System.out.println("success");
            return new Result(true,"更新成功");
        }catch (Exception e){
            System.out.println("error");
            return new Result(false,"更新失败");
        }
    }
    @RequestMapping("/delete")
    public Result specDelete(Long[] selectList){
        try {
            specService.specDelete(selectList);
            System.out.println("success");
            return new Result(true,"更新成功");
        }catch (Exception e){
            System.out.println("error");
            return new Result(false,"更新失败");
        }
    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return specService.selectOptionList();
    }


}
