package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.pojo.TbUserExample;
import com.pinyougou.user.service.SafeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class SafeServiceImpl implements SafeService {


    @Autowired
    private TbUserMapper tbUserMapper;


    @Override
    public String phoneInit(String sellerName) {
        TbUserExample example=new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(sellerName);
        String phone = null;
        try {
            List<TbUser> tbUsers = tbUserMapper.selectByExample(example);
            phone = tbUsers.get(0).getPhone();
        } catch (Exception e) {
            phone="请先验证手机号码";
          return phone;
        }
       return phone;
    }
}
