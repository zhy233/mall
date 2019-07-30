package com.macro.mall.service;

import com.macro.mall.common.api.CommonResult;

/**
 * 商品短链接口
 * Create by zhuyong on 2019/7/21
 */
public interface PmsProductShortChainService {

    CommonResult createShortChain(Long employeeId, Long productId);

    CommonResult getShortChainList(Long productId);

    CommonResult getMainDomainUrl(Long valueOf, Long valueOf1);
}
