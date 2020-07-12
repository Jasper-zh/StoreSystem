package com.hao.core.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.entity.Result;
import com.hao.core.pojo.template.TypeTemplate;
import com.hao.core.service.TempService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/temp")
public class TempController {
    @Reference
    TempService tempService;

    @RequestMapping("getPage")
    public PageResult getPage(Integer page, Integer pageSize, @RequestBody TypeTemplate temp){
        return tempService.getPage(page,pageSize,temp);
    }
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate template) {
        try {
            tempService.addTemp(template);
            return new Result(true, "添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败!");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long[] selectList){
        try {
            System.out.println(selectList);
            tempService.tempDelete(selectList);
            System.out.println("success");
            return new Result(true,"更新成功");
        }catch (Exception e){
            System.out.println("error");
            return new Result(false,"更新失败");
        }
    }
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
            System.out.println(id);
            return tempService.findOne(id);
    }
    @RequestMapping("/findBySpecList")
    public List<Map> findBySpecList(Long id){
        List<Map> mapList = tempService.findBySpecList(id);
        System.out.println(mapList.toString());
        return mapList;
    }

}
