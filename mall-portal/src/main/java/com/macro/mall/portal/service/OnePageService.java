package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.OnePageOrderDetail;
import com.macro.mall.portal.domain.OnePageProductResult;
import com.macro.mall.portal.domain.OrderSubmitParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 单页电商营销Service
 * Create by zhuyong on 2019/7/14
 */
public interface OnePageService {

    OnePageProductResult content(Long productId);

    List<OnePageOrderDetail> getOrderDetail(String conditions);

    CommonResult doWeChatPayByThirdParty(String orderNo, Long orderId, String frontUrl, String type, String body);

    CommonResult paySuccess(String orderNo);

    CommonResult paySuccessHandle(String orderNo,String trade_no);

    CommonResult submitOrder(OrderSubmitParam orderSubmitParam);

    String getRedirectUrl(Long employeeId, Long productId);

    CommonResult isPayWxPay(String subject, Long orderId, String attachData, String orderNumber, String return_url);

    String verifyCallBackSign(String orderNumber, String payChannel, Integer money, String attachData, String callbackSign);

    CommonResult queryOrder(String orderNumber);

    CommonResult getPayWxPayParams(String subject, Long orderId, String attachData, String orderNumber, String return_url);

    /**
     * 统计员工商品码被访问次数
     * Create by zhuyong on 2019/7/27
     */
    void countVisitTimes(Long employeeId, Long productId);

    String paySuccess2(String orderNumber, String result) throws Exception;

    void insertTaskLog(String orderNumber);

    Map<String,Object> jiujiuPayWxPay(HttpServletRequest request) throws Exception;

    Map<String, Object> jiujiuPaySuccess(HttpServletRequest request);
}
