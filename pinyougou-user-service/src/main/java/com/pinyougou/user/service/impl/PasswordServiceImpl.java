package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.pojo.TbUserExample;
import com.pinyougou.user.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import util.MD5Utils;

import java.util.List;

@Service
public class PasswordServiceImpl implements PasswordService {
    @Autowired
    private TbUserMapper userMapper;

    @Override
    public void changePwd(String username, String password) {
        TbUserExample example=new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> tbUsers = userMapper.selectByExample(example);
        TbUser user = tbUsers.get(0);
        String s = MD5Utils.MD5Encode(password,"utf-8");
        user.setPassword(s);
        userMapper.updateByPrimaryKeySelective(user);
    }
}
