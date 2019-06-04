package com.pinyougou.user.service;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.pojo.TbUser;

import entity.PageResult;
import entity.Result;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbUser> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbUser user);
	
	
	/**
	 * 修改
	 */
	public void update(TbUser user);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbUser findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbUser user, int pageNum,int pageSize);
	
	
	/**
	 * 发送短信验证码
	 * @param phone
	 */
	public void createSmsCode(String phone);
	
	/**
	 * 校验验证码
	 * @param phone
	 * @param code
	 * @return
	 */
	public boolean checkSmsCode(String phone,String code);
	//查询指定用户名的数据
	public TbUser initial(String username) throws ParseException;
	//查询省
	List<TbProvinces> province();
	//查市
	List<TbCities> city(String provinceId);
	//查区
	List<TbAreas> areas(String cityId);
	//修改
	void save(TbUser user);

}
