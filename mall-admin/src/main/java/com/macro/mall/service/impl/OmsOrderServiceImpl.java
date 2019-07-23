package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dao.OmsOrderDao;
import com.macro.mall.dao.OmsOrderOperateHistoryDao;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.EmsEmployeeMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单管理Service实现类
 * Created by macro on 2018/10/11.
 */
@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderDao orderDao;
    @Autowired
    private OmsOrderOperateHistoryDao orderOperateHistoryDao;
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    @Autowired
    private EmsEmployeeMapper employeeMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    @Override
    public List<OmsOrder> list(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return orderDao.getList(queryParam);
    }

    @Override
    public int delivery(List<OmsOrderDeliveryParam> deliveryParamList) {
        //批量发货
        int count = orderDao.delivery(deliveryParamList);
        //添加操作记录
        List<OmsOrderOperateHistory> operateHistoryList = deliveryParamList.stream()
                .map(omsOrderDeliveryParam -> {
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(omsOrderDeliveryParam.getOrderId());
                    history.setCreateTime(new Date());
                    history.setOperateMan("后台管理员");
                    history.setOrderStatus(2);
                    history.setNote("完成发货");
                    return history;
                }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(operateHistoryList);
        return count;
    }

    @Override
    public int close(List<Long> ids, String note) {
        OmsOrder record = new OmsOrder();
        record.setStatus(4);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        int count = orderMapper.updateByExampleSelective(record, example);
        List<OmsOrderOperateHistory> historyList = ids.stream().map(orderId -> {
            OmsOrderOperateHistory history = new OmsOrderOperateHistory();
            history.setOrderId(orderId);
            history.setCreateTime(new Date());
            history.setOperateMan("后台管理员");
            history.setOrderStatus(4);
            history.setNote("订单关闭:"+note);
            return history;
        }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(historyList);
        return count;
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrder record = new OmsOrder();
        record.setDeleteStatus(1);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        return orderMapper.updateByExampleSelective(record, example);
    }

    @Override
    public OmsOrderDetail detail(Long id) {
        return orderDao.getDetail(id);
    }

    @Override
    public int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(receiverInfoParam.getOrderId());
        order.setReceiverName(receiverInfoParam.getReceiverName());
        order.setReceiverPhone(receiverInfoParam.getReceiverPhone());
        order.setReceiverPostCode(receiverInfoParam.getReceiverPostCode());
        order.setReceiverDetailAddress(receiverInfoParam.getReceiverDetailAddress());
        order.setReceiverProvince(receiverInfoParam.getReceiverProvince());
        order.setReceiverCity(receiverInfoParam.getReceiverCity());
        order.setReceiverRegion(receiverInfoParam.getReceiverRegion());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(receiverInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(receiverInfoParam.getStatus());
        history.setNote("修改收货人信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(moneyInfoParam.getOrderId());
        order.setFreightAmount(moneyInfoParam.getFreightAmount());
        order.setDiscountAmount(moneyInfoParam.getDiscountAmount());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(moneyInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(moneyInfoParam.getStatus());
        history.setNote("修改费用信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateNote(Long id, String note, Integer status) {
        OmsOrder order = new OmsOrder();
        order.setId(id);
        order.setNote(note);
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(id);
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(status);
        history.setNote("修改备注信息："+note);
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public List<OmsOrderExportResult> orderExport(List<Long> orderIds) throws Exception {
        OmsOrderExample orderExample = new OmsOrderExample();
        orderExample.createCriteria().andIdIn(orderIds);
        List<OmsOrder> orderList = orderMapper.selectByExample(orderExample);
        List<OmsOrderExportResult> resultList = new ArrayList<>();
        for (OmsOrder order : orderList) {
            OmsOrderExportResult exportResult = new OmsOrderExportResult();

            exportResult.setId(order.getId());
            exportResult.setOrderSn(order.getOrderSn());
            exportResult.setCreateTime(order.getCreateTime());
            exportResult.setMemberUsername(order.getMemberUsername());
            exportResult.setPayAmount(order.getPayAmount());
            exportResult.setPayType(order.getPayType());
            exportResult.setSourceType(order.getSourceType());
            exportResult.setStatus(order.getStatus());
            exportResult.setPaymentTime(order.getPaymentTime());

            exportResult.setSubmitTime(DateUtil.getString(order.getCreateTime()));
            if (order.getPaymentTime() == null) {
                exportResult.setPayTime("");
            } else {
                exportResult.setPayTime(DateUtil.getString(order.getPaymentTime()));
            }
            //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
            String orderStatus = "";
            switch (order.getStatus()) {
                case 0:
                    orderStatus = "待付款";
                    break;
                case 1:
                    orderStatus = "待发货";
                    break;
                case 2:
                    orderStatus = "已发货";
                    break;
                case 3:
                    orderStatus = "已完成";
                    break;
                case 4:
                    orderStatus = "已关闭";
                    break;
                case 5:
                    orderStatus = "无效订单";
                    break;

            }
            //支付方式：0->未支付；1->支付宝；2->微信
            String payTypeValue = "";
            switch (order.getPayType()) {
                case 0:
                    payTypeValue = "待付款";
                    break;
                case 1:
                    payTypeValue = "支付宝";
                    break;
                case 2:
                    payTypeValue = "微信";
                    break;
            }
            //订单来源：0->PC订单；1->app订单 2->微信H5订单
            String sourceTypeValue = "";
            if (order.getSourceType() == 0) {
                sourceTypeValue = "PC订单";
            } else if (order.getSourceType() == 1) {
                sourceTypeValue = "app订单";
            } else if (order.getSourceType() == 2) {
                sourceTypeValue = "微信H5订单";
            }
            exportResult.setOrderStatus(orderStatus);
            exportResult.setPayTypeValue(payTypeValue);
            exportResult.setSourceTypeValue(sourceTypeValue);
            resultList.add(exportResult);
        }
        return resultList;
    }

    @Override
    public CommonResult getOrderStatistic() {
        List<OrderStatisticResult> resultList = new ArrayList<>();
        EmsEmployeeExample employeeExample = new EmsEmployeeExample();
        employeeExample.createCriteria().andStatusEqualTo(0);
        List<EmsEmployee> employees = employeeMapper.selectByExample(employeeExample);
        for (EmsEmployee employee : employees) {
            OrderStatisticResult result = new OrderStatisticResult();
            //员工的总订单数
            OmsOrderExample example = new OmsOrderExample();
            OmsOrderExample.Criteria criteria = example.createCriteria();
            criteria.andEmployeeIdEqualTo(employee.getId()).andStatusBetween(1, 4);
            long allOrderNum = orderMapper.countByExample(example);

            String today = DateUtil.getYYYYMMDD(new Date());
            Date startDate = DateUtil.changeToYYYYMMDDHHMMSSDate(today + " 00:00:00");
            Date endDate = DateUtil.changeToYYYYMMDDHHMMSSDate(today + " 23:59:59");

            //员工的今日订单数
            OmsOrderExample example2 = new OmsOrderExample();
            OmsOrderExample.Criteria criteria2 = example2.createCriteria();
            criteria2.andEmployeeIdEqualTo(employee.getId())
                    .andStatusBetween(1, 4)
                    .andCreateTimeBetween(startDate, endDate);
            long todayOrderNum = orderMapper.countByExample(example2);

            List<OmsOrder> orderList = orderMapper.selectByExample(example2);
            int allItemNum = 0; //订单商品总件数
            for (OmsOrder order : orderList) {
                //员工的今日商品件数
                OmsOrderItemExample itemExample = new OmsOrderItemExample();
                itemExample.createCriteria().andOrderIdEqualTo(order.getId());
                List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(itemExample);
                int itemNum = 0; //订单项商品件数
                for (OmsOrderItem item : orderItemList) {
                    itemNum += item.getProductQuantity();
                }
                allItemNum += itemNum;
            }

            result.setEmployeeId(employee.getId());
            result.setEmployeeName(employee.getLoginName());
            result.setTodayOrderNum(todayOrderNum);
            result.setTodayProductNum(allItemNum);
            result.setAllOrderNum(allOrderNum);

            resultList.add(result);
        }

        return CommonResult.success(resultList);
    }
}
