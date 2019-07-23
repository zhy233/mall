package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.IsPayCallBackParam;
import com.macro.mall.portal.domain.OnePageOrderDetail;
import com.macro.mall.portal.domain.OnePageProductResult;
import com.macro.mall.portal.domain.OrderSubmitParam;

import java.util.List;

/**
 * 单页电商营销Service
 * Create by zhuyong on 2019/7/14
 */
public interface OnePageService {

    OnePageProductResult content(Long productId);

    List<OnePageOrderDetail> getOrderDetail(String conditions);

    CommonResult doWeChatPayByThirdParty(String orderNo, Long orderId, String frontUrl, String type, String body);

    CommonResult paySuccess(String orderNo);

    CommonResult submitOrder(OrderSubmitParam orderSubmitParam);

    String getRedirectUrl(Long employeeId, Long productId);

    CommonResult isPayWxPay(String subject, Long orderId, String attachData, String orderNumber, String return_url);

    String verifyCallBackSign(IsPayCallBackParam callBackParam);
}
