package com.pinyougou.manager.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pinyougou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbOrder;


import entity.PageResult;
import entity.Result;
import util.DateUtils;

import javax.jms.*;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbOrder> findAll() {
        return orderService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return orderService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param order
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbOrder order) {
        try {
            orderService.add(order);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param order
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbOrder order) {
        try {
            orderService.update(order);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbOrder findOne(Long id) {
        return orderService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            orderService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbOrder order, int page, int rows) {
        return orderService.findPage(order, page, rows);
    }

    /**
     * 数据导出 --》  Excel
     *
     * @param order
     * @return
     */

    @RequestMapping("/exportExcel")
    public Result exportExcel(@RequestBody TbOrder order) {
        try {
            orderService.exportExcel(order);
            return new Result(true, "导出成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "导出失败");
        }

    }

    /**
     * 根据时间查询订单
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return
     */
    @RequestMapping("/findDateByOrder")
    public List<TbOrder> findDateByOrder(String start, String end) {
        Date startDate = null;
        Date endDate = null;
        //给默认值
        if (end == null || end.length() <= 0) {  //结束时间
            endDate = new Date();
        } else {
            endDate = DateUtils.StringToDate(end, "yyyy-MM-dd");
        }

        if (start == null || start.length() <= 0) { //开始时间
            startDate = DateUtils.getPastDate(7);
        } else {
            startDate = DateUtils.StringToDate(start, "yyyy-MM-dd");
        }
        return orderService.findDateByOrder(startDate, endDate);
    }


    /**
     * 返回柱状图所需集合
     *
     * @param start
     * @param end
     * @return
     */
    @RequestMapping("/findDateByOrderType")
    public Map<String, Integer[]> findDateByOrderType(String start, String end) {
        Date startDate = null;
        Date endDate = null;
        //给默认值
        if (end == null || end.length() <= 0) {  //结束时间
            endDate = new Date();
        } else {
            endDate = DateUtils.StringToDate(end, "yyyy-MM-dd");
        }

        if (start == null || start.length() <= 0) { //开始时间
            startDate = DateUtils.getPastDate(7);
        } else {
            startDate = DateUtils.StringToDate(start, "yyyy-MM-dd");
        }
        System.out.println("开始时间:"+startDate);
        System.out.println("结束时间:"+endDate);
        return orderService.findDateByOrderType(startDate, endDate);
    }


}
