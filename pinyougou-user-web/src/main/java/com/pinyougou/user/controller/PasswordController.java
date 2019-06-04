package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.user.service.PasswordService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password")
public class PasswordController {
    @Reference
    private PasswordService passwordService;
    @RequestMapping("/change")
    public Result changePwd(String username,String password,String pwd){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        try {
            System.out.println(username+password+pwd);
            if (!name.equals(username)){
                return new Result(false,"用户名输入不正确");
            }
            if (!password.equals(pwd)){
                return new Result(false,"密码输入不一致");
            }
            passwordService.changePwd(username,password);
            return new Result(true,"密码修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"请重新输入验证名或密码");

        }
    }

}
