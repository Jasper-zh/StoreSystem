package com.hao.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hao.core.pojo.entity.Result;
import com.hao.core.service.UserService;
import com.hao.core.utils.PhoneFormatCheckUtils;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Reference
    UserService userService;

    @RequestMapping("/sendCode")
    public Result sendCode(String phone) {
        try {
            if (phone == null || "".equals(phone)) {
                return new Result(false, "手机号不能为空!");
            }
            if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
                return new Result(false, "手机号格式不正确");
            }
            userService.sendCode(phone);
            return  new Result(true, "发送成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "发送失败!");
        }
    }
}
