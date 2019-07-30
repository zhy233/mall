package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.service.OmsOrderService;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Create by zhuyong on 2019/7/29
 */
@Controller
@Api(tags = "OmsPayController", description = "订单支付Controller")
@RequestMapping("/pay")
public class OmsPayController {
    @Autowired
    private OmsOrderService orderService;

    @ApiOperation("久久微信支付")
    @RequestMapping(value = "/jiujiuPayWxPay", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult jiujiuPayWxPay(HttpServletRequest request, HttpServletResponse response) {
        try{
            Map<String, Object> resultMap = orderService.jiujiuPayWxPay(request);
            return CommonResult.success(resultMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CommonResult.failed();
    }

}
