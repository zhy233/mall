package com.macro.mall.service;

import com.macro.mall.common.api.CommonResult;

/**
 * 域名Service
 * Create by zhuyong on 2019/7/20
 */
public interface DmsDomainService {

    CommonResult getDomainSettingInfo();

    CommonResult updateMainDomain(String mainDomain,String landingDomain);
}
