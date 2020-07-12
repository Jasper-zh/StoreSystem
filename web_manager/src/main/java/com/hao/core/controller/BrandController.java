package com.hao.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.entity.Result;
import com.hao.core.pojo.good.Brand;
import com.hao.core.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    BrandService brandService;
    @RequestMapping("/getAll")
    public List<Brand> getAllBrand(){
        System.out.println(brandService.getAllBrand());
        return brandService.getAllBrand();
    }
    @RequestMapping("/getPage")
    public PageResult getPage(@RequestBody Brand brand, Integer page, Integer pageSize){
        return brandService.getPage(brand,page,pageSize);
    }

    @RequestMapping("/add")
    public Result getPage(@RequestBody Brand brand){
        try{
            brandService.add(brand);
            return new Result(true,"插入成功");
        }catch (Exception e){
            return new Result(false,"插入失败");
        }

    }
    @RequestMapping("/getBrandById")
    public Brand getBrandById(Long id){
        return brandService.getBrandById(id);
    }
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return new Result(true,"更新成功");
        }catch (Exception e){
            return new Result(false,"更新失败");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long[] selectList){
        try{
            brandService.delete(selectList);
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
