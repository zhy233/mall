package com.macro.mall.dto;

import com.macro.mall.model.OmsOrder;

/**
 * 订单导出数据
 * Create by zhuyong on 2019/7/21
 */
public class OmsOrderExportResult extends OmsOrder {
    private String submitTime;
    private String payTypeValue;
    private String sourceTypeValue;
    private String orderStatus;
    private String payTime;
    private String productAttr;

    public String getProductAttr() {
        return productAttr;
    }

    public void setProductAttr(String productAttr) {
        this.productAttr = productAttr;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public String getPayTypeValue() {
        return payTypeValue;
    }

    public void setPayTypeValue(String payTypeValue) {
        this.payTypeValue = payTypeValue;
    }

    public String getSourceTypeValue() {
        return sourceTypeValue;
    }

    public void setSourceTypeValue(String sourceTypeValue) {
        this.sourceTypeValue = sourceTypeValue;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }
}
