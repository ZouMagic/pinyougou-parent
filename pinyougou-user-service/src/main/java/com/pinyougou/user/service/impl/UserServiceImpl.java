package com.pinyougou.user.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import entity.Result;
import entity.TbInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbUserExample.Criteria;
import com.pinyougou.user.service.UserService;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbUser> findAll() {
        return userMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbUser> page = (Page<TbUser>) userMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbUser user) {

        user.setCreated(new Date());//用户注册时间
        user.setUpdated(new Date());//修改时间
        user.setSourceType("1");//注册来源
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));//密码加密

        userMapper.insert(user);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbUser user) {
        userMapper.updateByPrimaryKey(user);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbUser findOne(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            userMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbUser user, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbUserExample example = new TbUserExample();
        Criteria criteria = example.createCriteria();

        if (user != null) {
            if (user.getUsername() != null && user.getUsername().length() > 0) {
                criteria.andUsernameLike("%" + user.getUsername() + "%");
            }
            if (user.getPassword() != null && user.getPassword().length() > 0) {
                criteria.andPasswordLike("%" + user.getPassword() + "%");
            }
            if (user.getPhone() != null && user.getPhone().length() > 0) {
                criteria.andPhoneLike("%" + user.getPhone() + "%");
            }
            if (user.getEmail() != null && user.getEmail().length() > 0) {
                criteria.andEmailLike("%" + user.getEmail() + "%");
            }
            if (user.getSourceType() != null && user.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + user.getSourceType() + "%");
            }
            if (user.getNickName() != null && user.getNickName().length() > 0) {
                criteria.andNickNameLike("%" + user.getNickName() + "%");
            }
            if (user.getName() != null && user.getName().length() > 0) {
                criteria.andNameLike("%" + user.getName() + "%");
            }
            if (user.getStatus() != null && user.getStatus().length() > 0) {
                criteria.andStatusLike("%" + user.getStatus() + "%");
            }
            if (user.getHeadPic() != null && user.getHeadPic().length() > 0) {
                criteria.andHeadPicLike("%" + user.getHeadPic() + "%");
            }
            if (user.getQq() != null && user.getQq().length() > 0) {
                criteria.andQqLike("%" + user.getQq() + "%");
            }
            if (user.getIsMobileCheck() != null && user.getIsMobileCheck().length() > 0) {
                criteria.andIsMobileCheckLike("%" + user.getIsMobileCheck() + "%");
            }
            if (user.getIsEmailCheck() != null && user.getIsEmailCheck().length() > 0) {
                criteria.andIsEmailCheckLike("%" + user.getIsEmailCheck() + "%");
            }
            if (user.getSex() != null && user.getSex().length() > 0) {
                criteria.andSexLike("%" + user.getSex() + "%");
            }

        }

        Page<TbUser> page = (Page<TbUser>) userMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination smsDestination;

    @Value("${template_code}")
    private String template_code;

    @Value("${sign_name}")
    private String sign_name;

    @Override
    public void createSmsCode(final String phone) {
        //1.生成一个6位随机数（验证码）
        final String smscode = (long) (Math.random() * 1000000) + "";
        System.out.println("验证码：" + smscode);

        //2.将验证码放入redis
        redisTemplate.boundHashOps("smscode").put(phone, smscode);
        //3.将短信内容发送给activeMQ

        jmsTemplate.send(smsDestination, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage message = session.createMapMessage();
                message.setString("mobile", phone);//手机号
                message.setString("template_code", template_code);//验证码
                message.setString("sign_name", sign_name);//签名
                Map map = new HashMap();
                map.put("number", smscode);
                message.setString("param", JSON.toJSONString(map));
                return message;
            }
        });


    }

    @Override
    public boolean checkSmsCode(String phone, String code) {

        String systemcode = (String) redisTemplate.boundHashOps("smscode").get(phone);
        if (systemcode == null) {
            return false;
        }
        if (!systemcode.equals(code)) {
            return false;
        }

        return true;
    }

    @Autowired
    private TbAddressMapper addressMapper;

    @Override
    public TbUser initial(String username) throws ParseException {

        TbUserExample userExample = new TbUserExample();

        Criteria criteria = userExample.createCriteria();

        criteria.andUsernameEqualTo(username);

        List<TbUser> tbUsers = userMapper.selectByExample(userExample);
        //设置时间字符串
        TbUser tbUser = tbUsers.get(0);

        Date birthday = tbUser.getBirthday();

        System.out.println("1" + birthday);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String format = simpleDateFormat.format(birthday);

        tbUser.setBirthdayStr(format);

        birthday.getTime();

        //查询对应的Add表单


        return tbUser;

    }

    @Autowired
    private TbCitiesMapper citiesMapper;

    //查市
    @Override
    public List<TbCities> city(String provinceId) {

        TbCitiesExample citiesExample = new TbCitiesExample();

        TbCitiesExample.Criteria criteria = citiesExample.createCriteria();

        criteria.andProvinceidEqualTo(provinceId);

        List<TbCities> tbCities = citiesMapper.selectByExample(citiesExample);

        return tbCities;

    }

    @Autowired
    private TbAreasMapper areasMapper;

    //查区
    @Override
    public List<TbAreas> areas(String cityId) {

        TbAreasExample areasExample = new TbAreasExample();
        TbAreasExample.Criteria criteria = areasExample.createCriteria();
        criteria.andCityidEqualTo(cityId);
        List<TbAreas> tbAreas = areasMapper.selectByExample(areasExample);
        return tbAreas;
    }

    //修改
    @Override
    public void save(TbUser user) {

        try {
            String birthdayStr = user.getBirthdayStr();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date parse = simpleDateFormat.parse(birthdayStr);

            user.setBirthday(parse);

            user.setUpdated(new Date());

            userMapper.updateByPrimaryKey(user);

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Autowired
    private TbProvincesMapper provincesMapper;

    //查省
    @Override
    public List<TbProvinces> province() {

        List<TbProvinces> Provinces = provincesMapper.selectByExample(null);

        return Provinces;
    }

}
