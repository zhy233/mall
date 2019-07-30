package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.api.ResultCode;
import com.macro.mall.service.PmsProductShortChainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品短链Controller
 * Create by zhuyong on 2019/7/21
 */
@Controller
@Api(tags = "PmsProductShortChainController", description = "短链管理")
@RequestMapping("/shortChain")
public class PmsProductShortChainController {

    @Autowired
    private PmsProductShortChainService shortChainService;

    @Deprecated
    @ApiOperation("生成短链")
    @RequestMapping(value = "/createShortChain",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult createShortChain(@RequestParam("employeeId") Long employeeId,
                                         @RequestParam("productId") Long productId){
        return  shortChainService.createShortChain(employeeId,productId);
    }


    @ApiOperation("获取长链")
    @RequestMapping(value = "/getLongChain")
    @ResponseBody
    public CommonResult createShortChain(HttpServletRequest request,
                                         HttpServletRequest response){
        String employeeId = request.getParameter("employeeId");
        String productId = request.getParameter("productId");
        if(StringUtils.isEmpty(employeeId)
                && StringUtils.isEmpty(productId)){
            return CommonResult.failed(ResultCode.VALIDATE_FAILED);
        }
        return  shortChainService.getMainDomainUrl(Long.valueOf(employeeId),Long.valueOf(productId));
    }

    @ApiOperation("短链列表")
    @RequestMapping(value = "/getShortChainList",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult getShortChainList(@RequestParam("productId") Long productId){
        return  shortChainService.getShortChainList(productId);
    }



}
