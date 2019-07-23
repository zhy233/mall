package com.macro.mall.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单导出请求参数
 * Create by zhuyong on 2019/7/21
 */
public class OrderExportParam {
    List<Long> orderIds=new ArrayList<>(); //id集合

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }
}
