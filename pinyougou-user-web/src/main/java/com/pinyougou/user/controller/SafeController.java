package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.user.service.SafeService;
import com.pinyougou.user.service.UserService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/safe")
public class SafeController {
    @Reference
    private SafeService safeService;

    @Reference
    private UserService userService;

    @RequestMapping("/init")
    public Map phoneInit(){
        HashMap<String, Object> map = new HashMap<>();
        String sellerName = SecurityContextHolder.getContext().getAuthentication().getName();

        String phone = safeService.phoneInit(sellerName);
        if (phone==null){
            map.put("phone",0);
            return map;
        }
        map.put("phone",phone);
        System.out.println(map);
        return map;
    }


    @RequestMapping("/send")
    public void phoneCodeSend(String phone){
        String sellerName = SecurityContextHolder.getContext().getAuthentication().getName();
            if (sellerName!=null&sellerName.length()>0){
                userService.createSmsCode(phone);
            }
    }

    @RequestMapping("/check")
    public Result phoneCodeCHeck(String phone, String code){
        try {
            System.out.println(phone+"--"+code);
            boolean b = userService.checkSmsCode(phone, code);
            System.out.println(b);
            if (b){
            return new Result(true,"请修改密码")
                    ; }
            else
                return new Result(false,"验证码错误");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"验证码错误");
        }
    }

}
