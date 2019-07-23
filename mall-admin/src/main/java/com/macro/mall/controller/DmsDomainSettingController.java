package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.service.DmsDomainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Create by zhuyong on 2019/7/20
 */
@Controller
@Api(tags = "DmsDomainSettingController", description = "域名管理")
@RequestMapping("/domain")
public class DmsDomainSettingController {
    @Autowired
    private DmsDomainService  domainService;

    @ApiOperation("获取域名相关信息")
    @RequestMapping(value = "/getDomainSettingInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult getDomainSettingInfo() {
        return CommonResult.success(domainService.getDomainSettingInfo());
    }

    @ApiOperation("入口、落地域名设置")
    @RequestMapping(value = "/updateDomainSetting",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateDomainSetting(@RequestParam String mainDomain,
                                            @RequestParam String landingDomain){
        if(StringUtils.isEmpty(mainDomain)){
            return CommonResult.failed("入口域名不能为空");
        }
        if(StringUtils.isEmpty(landingDomain)){
            return CommonResult.failed("落地域名不能为空");
        }
        return  domainService.updateMainDomain(mainDomain,landingDomain);
    }



}
