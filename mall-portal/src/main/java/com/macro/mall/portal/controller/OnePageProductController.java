package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.OmsPortalOrderReturnApplyService;
import com.macro.mall.portal.service.OnePageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 单页电商营销controllr
 * Create by zhuyong on 2019/7/13
 */
@Controller
@Api(tags = "OnePageProductController", description = "单页电商营销controllr")
@RequestMapping("/onePage")
public class OnePageProductController {

    @Autowired
    private OnePageService onePageService;

    @Autowired
    private OmsPortalOrderReturnApplyService returnApplyService;


    @ApiOperation("单品页面显示")
    @RequestMapping(value = "/content/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OnePageProductResult> content(@PathVariable Long productId) {
        OnePageProductResult content = onePageService.content(productId);
        return CommonResult.success(content);
    }

    @ApiOperation("查询订单")
    @RequestMapping(value = "/getOrderDetail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OnePageOrderDetail>> getOrderDetail(@RequestParam(value = "conditions") String conditions) {
        List<OnePageOrderDetail> content = onePageService.getOrderDetail(conditions);
        return CommonResult.success(content);
    }

    @ApiOperation("订单提交")
    @RequestMapping(value = "/submitOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult submitOrder(@RequestBody OrderSubmitParam orderSubmitParam) {
        return CommonResult.success(onePageService.submitOrder(orderSubmitParam));
    }

    @ApiOperation("申请退货")
    @RequestMapping(value = "/applyReturn", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult applyReturn(@RequestBody OmsOrderReturnApplyParam returnApply) {

        int count = returnApplyService.create(returnApply);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("第三方微信支付")
    @RequestMapping(value = "/doWeChatPayByThirdParty", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult wxPay(@RequestBody WxPayParam payParam) {
        CommonResult commonResult = onePageService.doWeChatPayByThirdParty(
                        payParam.getOrderNo(),
                        payParam.getOrderId(),
                        payParam.getFrontUrl(),
                        payParam.getType(),
                        payParam.getBody());
        return commonResult;
    }

    @ApiOperation("支付成功的回调")
    @RequestMapping(value = "/paySuccess",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult paySuccess(@RequestParam String response){
        JSONObject jsonObject = JSONObject.fromObject(response);
        String orderNo = (String)jsonObject.get("orderNo");
        return onePageService.paySuccess(orderNo);
    }

    @ApiOperation("落地域名重定向")
    @RequestMapping(value = "/redirect",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult redirect(@RequestBody RedirectParam redirectParam,
                         HttpServletResponse response) throws IOException {
        String url = onePageService.getRedirectUrl(redirectParam.getEmployeeId(),redirectParam.getProductId());
        return CommonResult.success(url);
    }


    @ApiOperation("ISPAY微信支付")
    @RequestMapping(value = "/isPayWxPay", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult isPayWxPay(@RequestBody IsPayParam payParam) {
        CommonResult commonResult = onePageService.isPayWxPay(
                payParam.getSubject(),
                payParam.getOrderId(),
                payParam.getAttachData(),
                payParam.getOrderNumber(),
                payParam.getReturn_url());
        return commonResult;
    }

    @ApiOperation("ISPAY支付成功的回调")
    @RequestMapping(value = "/isPaySuccess",method = RequestMethod.POST)
    @ResponseBody
    public String isPaySuccess(@RequestBody IsPayCallBackParam callBackParam){
        String orderNo = callBackParam.getOrderNumber();

        String result = onePageService.verifyCallBackSign(callBackParam);
        if("SUCCESS".equals(result)){
            CommonResult commonResult = onePageService.paySuccess(orderNo);
            System.out.println("isPaySuccess ==== orderNo:"+callBackParam.getOrderNumber());
            if(commonResult.getCode() != 200){
                return null;
            }
        }
        return result;
    }
}
