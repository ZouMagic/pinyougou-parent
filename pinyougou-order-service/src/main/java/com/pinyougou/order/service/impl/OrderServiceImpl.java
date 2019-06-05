package com.pinyougou.order.service.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;

import entity.Excel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.group.Cart;
import com.pinyougou.order.service.OrderService;

import entity.PageResult;
import util.DateUtils;
import util.ExportExcelUtil;
import util.IdWorker;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {

        //1.从redis中提取购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        List<String> orderIdList = new ArrayList();//订单ID集合
        double total_money = 0;//总金额
        //2.循环购物车列表添加订单
        for (Cart cart : cartList) {
            TbOrder tbOrder = new TbOrder();
            long orderId = idWorker.nextId();    //获取ID
            tbOrder.setOrderId(orderId);
            tbOrder.setPaymentType(order.getPaymentType());//支付类型
            tbOrder.setStatus("1");//未付款
            tbOrder.setCreateTime(new Date());//下单时间
            tbOrder.setUpdateTime(new Date());//更新时间
            tbOrder.setUserId(order.getUserId());//当前用户
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货人地址
            tbOrder.setReceiverMobile(order.getReceiverMobile());//收货人电话
            tbOrder.setReceiver(order.getReceiver());//收货人
            tbOrder.setSourceType(order.getSourceType());//订单来源
            tbOrder.setSellerId(order.getSellerId());//商家ID

            double money = 0;//合计数
            //循环购物车中每条明细记录
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());//主键
                orderItem.setOrderId(orderId);//订单编号
                orderItem.setSellerId(cart.getSellerId());//商家ID
                orderItemMapper.insert(orderItem);
                money += orderItem.getTotalFee().doubleValue();
            }

            tbOrder.setPayment(new BigDecimal(money));//合计


            orderMapper.insert(tbOrder);

            orderIdList.add(orderId + "");
            total_money += money;
        }

        //添加支付日志
        if ("1".equals(order.getPaymentType())) {
            TbPayLog payLog = new TbPayLog();

            payLog.setOutTradeNo(idWorker.nextId() + "");//支付订单号
            payLog.setCreateTime(new Date());
            payLog.setUserId(order.getUserId());//用户ID
            payLog.setOrderList(orderIdList.toString().replace("[", "").replace("]", ""));//订单ID串
            payLog.setTotalFee((long) (total_money * 100));//金额（分）
            payLog.setTradeState("0");//交易状态
            payLog.setPayType("1");//微信
            payLogMapper.insert(payLog);

            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//放入缓存
        }


        //3.清除redis中的购物车
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getOrderId() != null && order.getOrderId() > 0) {  //订单Id
                criteria.andOrderIdEqualTo(order.getOrderId());
            }
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {  //订单状态
                criteria.andStatusEqualTo(order.getStatus());
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {  //收货人
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {  //订单来源
                criteria.andSourceTypeEqualTo(order.getSourceType());
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //1.修改支付日志的状态及相关字段
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());//支付时间
        payLog.setTradeState("1");//交易成功
        payLog.setTransactionId(transaction_id);//微信的交易流水号

        payLogMapper.updateByPrimaryKey(payLog);//修改
        //2.修改订单表的状态
        String orderList = payLog.getOrderList();// 订单ID 串
        String[] orderIds = orderList.split(",");

        for (String orderId : orderIds) {
            TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
            order.setStatus("2");//已付款状态
            order.setPaymentTime(new Date());//支付时间
            orderMapper.updateByPrimaryKey(order);
        }

        //3.清除缓存中的payLog
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());

    }

    /**
     * 数据导出 --》  Excel
     *
     * @param order
     * @return
     */
    @Override
    public void exportExcel(TbOrder order) {

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getOrderId() != null && order.getOrderId() > 0) {  //订单Id
                criteria.andOrderIdEqualTo(order.getOrderId());
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {  //订单状态
                criteria.andStatusEqualTo(order.getStatus());
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {  //收货人
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {  //订单来源
                criteria.andSourceTypeEqualTo(order.getSourceType());
            }
        }
        //查询数据
        List<TbOrder> tbOrders = orderMapper.selectByExample(example);
        ExportExcelUtil<Excel> util = new ExportExcelUtil<Excel>();

        //准备数据  解决工具类类型出错问题
        List<Excel> orders = new ArrayList<Excel>();
        for (TbOrder tbOrder : tbOrders) {
            Excel excel = new Excel();
            excel.setOrderId(tbOrder.getOrderId() + "");
            excel.setUserId(tbOrder.getUserId() + "");
            excel.setReceiver(tbOrder.getReceiver() + "");
            excel.setReceiverMobile(tbOrder.getReceiverMobile() + "");
            excel.setPayment(tbOrder.getPayment() + "");
            excel.setPaymentType(tbOrder.getPaymentType() + "");
            excel.setSourceType(tbOrder.getSourceType() + "");
            excel.setStatus(tbOrder.getStatus() + "");
            orders.add(excel);
        }

        //excel头文件
        String[] columnNames = {"订单编号", "用户账号", "收货人", "手机号", "订单金额", "支付方式", "订单来源", "订单状态"};
        try {
            IdWorker idWorker = new IdWorker();
            //生成Excel文件
            util.exportExcel("user export", columnNames, orders, new FileOutputStream("D:/" + idWorker.nextId() + ".xlsx"), ExportExcelUtil.EXCEl_FILE_2007);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("data export excel error");
        }

    }

    /**
     * 根据时间查询订单
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return
     */
    @Override
    public List<TbOrder> findDateByOrder(Date start, Date end) {
        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();
        if (start != null && end != null) {
            criteria.andCreateTimeBetween(start, end);  //根据时间查询
        }
        //order数据
        List<TbOrder> tbOrders = orderMapper.selectByExample(example);
        return tbOrders;
    }


    /**
     * 返回柱状图所需集合
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return
     */
    @Override
    public Map<String, Integer[]> findDateByOrderType(Date start, Date end) {
        List<TbOrder> tbOrders = findDateByOrder(start, end);
        //返回数据集合
        Map<String, Integer[]> map = new HashMap<String, Integer[]>();

        //获取两个时间段的所有日期作为Key
        List<String> days = DateUtils.getDays(DateUtils.DateToString(start, "yyyy-MM-dd"), DateUtils.DateToString(end, "yyyy-MM-dd"));

        //构建返回集合
        for (int i = 0; i < days.size(); i++) {

            //给数组初始值
            Integer[] typeArray = new Integer[5];
            for (int j = 0; j < 5; j++) {
                typeArray[j] = 0;
            }
            map.put(days.get(i), typeArray);
        }

        //判断创建时间在哪个时间段
        //遍历数据集合
        for (TbOrder order : tbOrders) {
            Date createTime = order.getCreateTime();
            String createTimeStr = DateUtils.DateToString(createTime, "yyyy-MM-dd");

            for (int i = 0; i < days.size(); i++) {
                String key = days.get(i);  //大Key
                if (createTimeStr.equals(key)) {  //大key与日期字符串比较
                    //判断5种状态
                    Integer[] data = map.get(key);
                    if ("1".equals(order.getStatus())) {
                        data[0] = data[0] + 1;  //类型值加一
                    } else if ("3".equals(order.getStatus())) {
                        data[1] = data[1] + 1;  //类型值加一
                    } else if ("4".equals(order.getStatus())) {
                        data[2] = data[2] + 1;  //类型值加一
                    } else if ("5".equals(order.getStatus())) {
                        data[3] = data[3] + 1;  //类型值加一
                    } else if ("6".equals(order.getStatus())) {
                        data[4] = data[4] + 1;  //类型值加一
                    }
                }
            }

        }
        System.out.println(map);
        return map;
    }

}
