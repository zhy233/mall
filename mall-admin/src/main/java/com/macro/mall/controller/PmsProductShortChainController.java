package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.service.PmsProductShortChainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @ApiOperation("生成短链")
    @RequestMapping(value = "/createShortChain",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult createShortChain(@RequestParam("employeeId") Long employeeId,
                                         @RequestParam("productId") Long productId){
        return  shortChainService.createShortChain(employeeId,productId);
    }

    @ApiOperation("短链列表")
    @RequestMapping(value = "/getShortChainList",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult getShortChainList(@RequestParam("productId") Long productId){
        return  shortChainService.getShortChainList(productId);
    }


}
