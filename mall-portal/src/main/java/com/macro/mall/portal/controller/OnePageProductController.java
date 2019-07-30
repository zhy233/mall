package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.component.OrderTaskThread;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.OmsPortalOrderReturnApplyService;
import com.macro.mall.portal.service.OnePageService;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    @RequestMapping(value = "/content/{productId}/{employeeId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OnePageProductResult> content(@PathVariable Long productId, @PathVariable Long employeeId) {
        OnePageProductResult content = onePageService.content(productId);
        onePageService.countVisitTimes(employeeId,productId);
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
    @RequestMapping(value = "/redirect",method = {RequestMethod.POST,RequestMethod.GET})
    public void redirect(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        Long employeeId = Long.valueOf(request.getParameter("employeeId"));
        Long productId = Long.valueOf(request.getParameter("productId"));

        String url = onePageService.getRedirectUrl(employeeId,productId);
        response.setStatus(301);
        response.setHeader( "Location", url);
        response.setHeader( "Connection", "close" );
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

    @ApiOperation("ISPAY统一下单参数")
    @RequestMapping(value = "/getPayWxPayParams", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult getPayWxPayParams(@RequestBody IsPayParam payParam) {
        CommonResult commonResult = onePageService.getPayWxPayParams(
                payParam.getSubject(),
                payParam.getOrderId(),
                payParam.getAttachData(),
                payParam.getOrderNumber(),
                payParam.getReturn_url());
        return commonResult;
    }


    @ApiOperation("ISPAY支付成功的回调(作废)")
    @RequestMapping(value = "/isPaySuccess", method = {RequestMethod.POST,RequestMethod.GET})
    public String isPaySuccess(@RequestParam String payChannel, //返回 支付通道 代码
                               @RequestParam Integer Money, //用户付款金额（单位分）
                               @RequestParam String orderNumber, //商户自主生成的订单号
                               @RequestParam String attachData, //商户自定义附加数据
                               @RequestParam String callbackSign //回调签名 查看算法
    ){

        String result = onePageService.verifyCallBackSign(orderNumber,payChannel,Money,attachData,callbackSign);
        if("SUCCESS".equals(result)){
            CommonResult commonResult = onePageService.paySuccess(orderNumber);
            System.out.println("isPaySuccess ==== orderNo:"+orderNumber);
            if(commonResult.getCode() != 200){
                return null;
            }
        }
        return result;
    }

    @ApiOperation("ISPAY支付前端回调")
    @RequestMapping(value = "/queryForPay",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryOrder(@RequestParam String orderNumber){
        if(StringUtils.isNotEmpty(orderNumber)){
            OrderTaskThread thread = new OrderTaskThread(onePageService);
            thread.paySuccess(orderNumber);
        }
        return CommonResult.success(1);
    }

    @ApiOperation("久久微信支付")
    @RequestMapping(value = "/jiujiuPayWxPay", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult jiujiuPayWxPay(HttpServletRequest request,HttpServletResponse response) {
        try{
            Map<String, Object> resultMap = onePageService.jiujiuPayWxPay(request);
            return CommonResult.success(resultMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CommonResult.failed();
    }

    @ApiOperation("久久微信支付成功的回调")
    @RequestMapping(value = "/jiujiuPaySuccess")
    public String jiujiuPaySuccess(HttpServletRequest request,HttpServletResponse response){
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>久久微信支付成功的回调开始>>>>>>>>>>>>>>");
        try{
            Map<String, Object> resultMap = onePageService.jiujiuPaySuccess(request);
            if(resultMap != null){
                String out_trade_no = (String)resultMap.get("out_trade_no");
                String trade_no = (String)resultMap.get("trade_no");
                if(StringUtils.isNotEmpty(out_trade_no)){
                    CommonResult commonResult = onePageService.paySuccessHandle(out_trade_no, trade_no);
                    if(commonResult.getCode() != 200l){
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>久久微信支付成功的回调失败>>>>>>>>>>>>>>");
                       return "";
                    }
                }
            }

            return "success";
        }catch (Exception ex){
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>久久微信支付成功的回调异常>>>>>>>>>>>>>>");
            ex.printStackTrace();
        }
        return "";
    }

}
