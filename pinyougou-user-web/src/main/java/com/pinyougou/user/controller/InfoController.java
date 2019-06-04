package com.pinyougou.user.controller;

import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/info")
public class InfoController {
    @Reference
    private UserService userService;

    @RequestMapping("/initial")
    public TbUser initial() throws ParseException {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        TbUser initial = userService.initial(name);

        return initial;
    }

    //市
    @RequestMapping("/city")
    public List<TbCities> city(String provinceId) {
        if (provinceId == null && provinceId.length() > 0) {
            return null;
        }
        return userService.city(provinceId);
    }

    //省
    @RequestMapping("/province")
    public List<TbProvinces> province() {
        List<TbProvinces> province = userService.province();

        return province;
    }

    //区-县
    @RequestMapping("/areas")
    public List<TbAreas> areas(String cityId) {
        if (cityId == null && cityId.length() > 0) {
            return null;
        }
        return userService.areas(cityId);

    }

    //修改
    @RequestMapping("/save")
    public Result save(@RequestBody TbUser user) {

        try {
            userService.save(user);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "失败");
        }


    }
}

