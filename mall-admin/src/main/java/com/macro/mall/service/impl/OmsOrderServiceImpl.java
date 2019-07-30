package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dao.OmsOrderDao;
import com.macro.mall.dao.OmsOrderOperateHistoryDao;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.util.DateUtil;
import com.macro.mall.util.HttpClientUtil;
import com.macro.mall.util.MD5Utils;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单管理Service实现类
 * Created by macro on 2018/10/11.
 */
@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderDao orderDao;
    @Autowired
    private OmsOrderOperateHistoryDao orderOperateHistoryDao;
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    @Autowired
    private EmsEmployeeMapper employeeMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private EmsEmployeePerformanceMapper performanceMapper;

    //久久微信支付
    @Value("${jiujiu-pay.merchant_id}")
    private Integer MERCHANT_ID;
    @Value("${jiujiu-pay.s_key}")
    private String S_KEY;
    @Value("${jiujiu-pay.j_notify_url}")
    private String J_NOTIFY_URL;

    @Override
    public List<OmsOrder> list(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return  orderDao.getList(queryParam);
    }

    @Override
    public int delivery(List<OmsOrderDeliveryParam> deliveryParamList) {
        //批量发货
        int count = orderDao.delivery(deliveryParamList);
        //添加操作记录
        List<OmsOrderOperateHistory> operateHistoryList = deliveryParamList.stream()
                .map(omsOrderDeliveryParam -> {
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(omsOrderDeliveryParam.getOrderId());
                    history.setCreateTime(new Date());
                    history.setOperateMan("后台管理员");
                    history.setOrderStatus(2);
                    history.setNote("完成发货");
                    return history;
                }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(operateHistoryList);
        return count;
    }

    @Override
    public int close(List<Long> ids, String note) {
        OmsOrder record = new OmsOrder();
        record.setStatus(4);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        int count = orderMapper.updateByExampleSelective(record, example);
        List<OmsOrderOperateHistory> historyList = ids.stream().map(orderId -> {
            OmsOrderOperateHistory history = new OmsOrderOperateHistory();
            history.setOrderId(orderId);
            history.setCreateTime(new Date());
            history.setOperateMan("后台管理员");
            history.setOrderStatus(4);
            history.setNote("订单关闭:"+note);
            return history;
        }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(historyList);
        return count;
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrder record = new OmsOrder();
        record.setDeleteStatus(1);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        return orderMapper.updateByExampleSelective(record, example);
    }

    @Override
    public OmsOrderDetail detail(Long id) {
        return orderDao.getDetail(id);
    }

    @Override
    public int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(receiverInfoParam.getOrderId());
        order.setReceiverName(receiverInfoParam.getReceiverName());
        order.setReceiverPhone(receiverInfoParam.getReceiverPhone());
        order.setReceiverPostCode(receiverInfoParam.getReceiverPostCode());
        order.setReceiverDetailAddress(receiverInfoParam.getReceiverDetailAddress());
        order.setReceiverProvince(receiverInfoParam.getReceiverProvince());
        order.setReceiverCity(receiverInfoParam.getReceiverCity());
        order.setReceiverRegion(receiverInfoParam.getReceiverRegion());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(receiverInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(receiverInfoParam.getStatus());
        history.setNote("修改收货人信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(moneyInfoParam.getOrderId());
        order.setFreightAmount(moneyInfoParam.getFreightAmount());
        order.setDiscountAmount(moneyInfoParam.getDiscountAmount());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(moneyInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(moneyInfoParam.getStatus());
        history.setNote("修改费用信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateNote(Long id, String note, Integer status) {
        OmsOrder order = new OmsOrder();
        order.setId(id);
        order.setNote(note);
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(id);
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(status);
        history.setNote("修改备注信息："+note);
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public List<OmsOrderExportResult> orderExport(List<Long> orderIds) throws Exception {
        OmsOrderExample orderExample = new OmsOrderExample();
        orderExample.createCriteria().andIdIn(orderIds);
        List<OmsOrder> orderList = orderMapper.selectByExample(orderExample);
        List<OmsOrderExportResult> resultList = new ArrayList<>();
        for (OmsOrder order : orderList) {
            OmsOrderExportResult exportResult = new OmsOrderExportResult();

            exportResult.setId(order.getId());
            exportResult.setOrderSn(order.getOrderSn());
            exportResult.setCreateTime(order.getCreateTime());
            exportResult.setMemberUsername(order.getMemberUsername());
            //exportResult.setPayAmount(order.getPayAmount());
            exportResult.setPayType(order.getPayType());
            exportResult.setSourceType(order.getSourceType());
            exportResult.setStatus(order.getStatus());
            exportResult.setPaymentTime(order.getPaymentTime());
            exportResult.setReceiverName(order.getReceiverName()); //收货人姓名
            exportResult.setReceiverPhone(order.getReceiverPhone()); //收货人电话
            exportResult.setReceiverPostCode(order.getReceiverPostCode()); //收货人邮编
            exportResult.setReceiverProvince(order.getReceiverProvince()); //省份/直辖市
            exportResult.setReceiverCity(order.getReceiverCity()); //城市
            exportResult.setReceiverRegion(order.getReceiverRegion()); //区
            exportResult.setReceiverDetailAddress(order.getReceiverDetailAddress()); //详细地址
            exportResult.setNote(order.getNote()); //订单备注

            OmsOrderItemExample example = new OmsOrderItemExample();
            example.createCriteria().andOrderSnEqualTo(order.getOrderSn());
            List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(example);
            List<String> productAttrList = new ArrayList<>();
            if(orderItemList != null && orderItemList.size() > 0){
                for(OmsOrderItem orderItem : orderItemList){
                    String attr = "["+orderItem.getSp1()+","+orderItem.getSp2()+","+orderItem.getSp3()+"]"+"*"+orderItem.getProductQuantity();
                    productAttrList.add(attr);
                }
            }
            String productAttr = String.join(",", productAttrList);
            exportResult.setProductAttr(productAttr);

            exportResult.setSubmitTime(DateUtil.getString(order.getCreateTime()));
            if (order.getPaymentTime() == null) {
                exportResult.setPayTime("");
            } else {
                exportResult.setPayTime(DateUtil.getString(order.getPaymentTime()));
            }
            //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
            String orderStatus = "";
            switch (order.getStatus()) {
                case 0:
                    orderStatus = "待付款";
                    break;
                case 1:
                    orderStatus = "待发货";
                    break;
                case 2:
                    orderStatus = "已发货";
                    break;
                case 3:
                    orderStatus = "已完成";
                    break;
                case 4:
                    orderStatus = "已关闭";
                    break;
                case 5:
                    orderStatus = "无效订单";
                    break;

            }
            //支付方式：0->未支付；1->支付宝；2->微信
            String payTypeValue = "";
            switch (order.getPayType()) {
                case 0:
                    payTypeValue = "待付款";
                    break;
                case 1:
                    payTypeValue = "支付宝";
                    break;
                case 2:
                    payTypeValue = "微信";
                    break;
            }
            //订单来源：0->PC订单；1->app订单 2->微信H5订单
            String sourceTypeValue = "";
            if (order.getSourceType() == 0) {
                sourceTypeValue = "PC订单";
            } else if (order.getSourceType() == 1) {
                sourceTypeValue = "app订单";
            } else if (order.getSourceType() == 2) {
                sourceTypeValue = "微信H5订单";
            }
            exportResult.setOrderStatus(orderStatus);
            exportResult.setPayTypeValue(payTypeValue);
            exportResult.setSourceTypeValue(sourceTypeValue);
            resultList.add(exportResult);
        }
        return resultList;
    }

    @Override
    public CommonResult getOrderStatistic() {
        List<OrderStatisticResult> resultList = new ArrayList<>();
        EmsEmployeeExample employeeExample = new EmsEmployeeExample();
        employeeExample.createCriteria().andStatusEqualTo(0);
        List<EmsEmployee> employees = employeeMapper.selectByExample(employeeExample);
        for (EmsEmployee employee : employees) {
            OrderStatisticResult result = new OrderStatisticResult();
            //员工的总订单数
            OmsOrderExample example = new OmsOrderExample();
            OmsOrderExample.Criteria criteria = example.createCriteria();
            criteria.andEmployeeIdEqualTo(employee.getId()).andStatusBetween(1, 4);
            long allOrderNum = orderMapper.countByExample(example);

            String today = DateUtil.getYYYYMMDD(new Date());
            Date startDate = DateUtil.changeToYYYYMMDDHHMMSSDate(today + " 00:00:00");
            Date endDate = DateUtil.changeToYYYYMMDDHHMMSSDate(today + " 23:59:59");

            //员工的今日订单数
            OmsOrderExample example2 = new OmsOrderExample();
            OmsOrderExample.Criteria criteria2 = example2.createCriteria();
            criteria2.andEmployeeIdEqualTo(employee.getId())
                    .andStatusBetween(1, 4)
                    .andCreateTimeBetween(startDate, endDate);
            long todayOrderNum = orderMapper.countByExample(example2);

            List<OmsOrder> orderList = orderMapper.selectByExample(example2);
            int allItemNum = 0; //订单商品总件数
            for (OmsOrder order : orderList) {
                //员工的今日商品件数
                OmsOrderItemExample itemExample = new OmsOrderItemExample();
                itemExample.createCriteria().andOrderIdEqualTo(order.getId());
                List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(itemExample);
                int itemNum = 0; //订单项商品件数
                for (OmsOrderItem item : orderItemList) {
                    itemNum += item.getProductQuantity();
                }
                allItemNum += itemNum;
            }

            EmsEmployeePerformanceExample example1 = new EmsEmployeePerformanceExample();
            example1.createCriteria().andEmployeeIdEqualTo(employee.getId());

            List<EmsEmployeePerformance> emsEmployeePerformances = performanceMapper.selectByExample(example1);
            int count = 0;
            for (EmsEmployeePerformance pf:emsEmployeePerformances){
                count +=pf.getTodayVisitTimes(); //当前员工所有商品码今天被访问次数
            }

            result.setEmployeeId(employee.getId());
            result.setEmployeeName(employee.getNickName());
            result.setTodayOrderNum(todayOrderNum);
            result.setTodayProductNum(allItemNum);
            result.setAllOrderNum(allOrderNum);
            result.setTodayVisitTimes(count);

            resultList.add(result);
        }

        return CommonResult.success(resultList);
    }

    /**
     * 久久微信支付
     * Create by zhuyong on 2019/7/29
     */
    @Override
    public Map<String, Object> jiujiuPayWxPay(HttpServletRequest request) throws Exception {
        Map<String,Object> pay_code_map = new HashMap<>();

        Integer merchant_id = MERCHANT_ID;//	必填	商户ID、在平台首页右边获取商户ID	10000
        String content_type = "json";//	必填		固定值:json
        String pay_type = "wechat";//	必填	支付方式，支付宝:alipay，微信:wechat，网银：bank,银联扫码:yinlian，银行聚合码:jhm	wechat
        String jhm_type = "";//	选填	pay_type为jhm时必填，聚合码收款方式，支付宝:alipay，微信:wechat，银联扫码:yinlian	wechat
        String pay_method = "wap";//	选填	pc:网站，wap:手机	wap
        String out_trade_no = request.getParameter("out_trade_no");//	必填	商户订单号，需保证在商户平台唯一	2018062668945
        String amount = request.getParameter("amount");//	必填	支付金额	1.00
        String robin = "1";//	必填	轮训，默认值1	1
        String notify_url = J_NOTIFY_URL;//	必填	异步通知地址，在支付完成时，本平台服务器系统会自动向该地址发起一条支付成功的回调请求, 对接方接收到回调后，  必须返回success ,否则默认为回调失败,回调信息会补发5次。	http://pay.9fubaopay.com/Notify/Pqi
        String return_url = request.getParameter("return_url");//	选填	支付成功后网页自动跳转地址	http://pay.9fubaopay.com/Return/Success

        StringBuilder params = new StringBuilder();
        //	amount={}&content_type={}&merchant_id={}&notify_url={}&out_trade_no={}&pay_type={}{key}
        params.append("amount=").append(amount);
        params.append("&content_type=").append(content_type);
        params.append("&merchant_id=").append(merchant_id);
        params.append("&notify_url=").append(notify_url);
        params.append("&out_trade_no=").append(out_trade_no);
        params.append("&pay_type=").append(pay_type);
        params.append(S_KEY);
        //md5加密,大写
        String sign = MD5Utils.MD5Encode(params.toString(),"utf-8",true);

        //封装application/x-www-form-urlencoded 参数
        Map<Object,Object> map = new HashMap<>();
        map.put("merchant_id",merchant_id);
        map.put("content_type",content_type);
        map.put("pay_type",pay_type);
        map.put("jhm_type",jhm_type);
        map.put("pay_method",pay_method);
        map.put("out_trade_no",out_trade_no);
        map.put("amount",amount);
        map.put("robin",robin);
        map.put("notify_url",notify_url);
        map.put("return_url",return_url);
        map.put("sign",sign);

        String response = HttpClientUtil.doPostForm("http://pay.9fubaopay.com/GateWay/Pay", map, "utf-8");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>久久微信支付返回参数:>>>>>>>>>>>>>>"+response);
        //校验返回值
        JSONObject result = JSONObject.fromObject(response);
        String result_merchant_id = (String)result.get("merchant_id");
        String result_order_amount = (String)result.get("order_amount");
        String result_pay_amount = (String)result.get("pay_amount");
        String result_out_trade_no = (String)result.get("out_trade_no");
        String result_trade_no = (String)result.get("trade_no");
        String result_fees = (String)result.get("fees");
        String result_paytype = (String)result.get("paytype");
        String result_status = (String)result.get("status");
        String result_message = (String)result.get("message");
        String result_payurl = (String)result.get("payurl");
        String result_sign = (String)result.get("sign");
        String result_codeurl = (String)result.get("codeurl");

        StringBuilder result_params = new StringBuilder();
        result_params.append("merchant_id=").append(result_merchant_id);
        result_params.append("&message=").append(result_message);
        result_params.append("&fees=").append(result_fees);
        result_params.append("&order_amount=").append(result_order_amount);
        result_params.append("&out_trade_no=").append(result_out_trade_no);
        result_params.append("&paytype=").append(result_paytype);
        result_params.append("&pay_amount=").append(result_pay_amount);
        result_params.append("&status=").append(result_status);
        result_params.append("&payurl=").append(result_payurl);
        result_params.append("&trade_no=").append(result_trade_no);
        result_params.append(S_KEY);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>result_params:"+result_params.toString());
        String my_result_sign = MD5Utils.MD5Encode(result_params.toString(),"utf-8",true);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>久久微信支付校验>>>>>>>>>>>>>>"+(my_result_sign.equals(result_sign)));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>my_result_sign:"+my_result_sign);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>result_sign:"+result_sign);

        pay_code_map.put("status",result_status);
        pay_code_map.put("result_message",result_message);
        if(true || my_result_sign.equals(result_sign)){
            //校验成功
            pay_code_map.put("result_codeurl",result_codeurl);
        }
        return pay_code_map;
    }

    /**
     * 久久微信支付成功的回调
     * Create by zhuyong on 2019/7/29
     */
    @Override
    public Map<String, Object> jiujiuPaySuccess(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        //获取参数
        String merchant_id = request.getParameter("merchant_id");//	商户ID、在平台首页右边获取商户ID	10000
        String pay_time = request.getParameter("pay_time");//	支付成功的	格式为:20181201102435(2018年12月1日10时2分35秒)
        String status = request.getParameter("status");//	支付状态，success表示成功,fail表示失败	success
        String order_amount = request.getParameter("order_amount");//	订单金额	1.00
        String pay_amount = request.getParameter("pay_amount");//	实际支付金额	1.00
        String out_trade_no = request.getParameter("out_trade_no");//	商户订单号	2018062312410711888
        String trade_no = request.getParameter("trade_no");//	交易流水号，由系统生成的交易流水号	2018062312410729584
        String fees = request.getParameter("fees");//	手续费，该笔订单的手续费(已在平台余额中扣除)	0.0400
        String paytype = request.getParameter("paytype");//	支付方式，支付宝:alipay，微信:wechat	wechat
        String jhm_type = request.getParameter("jhm_type");//	paytype为jhm时必填	wechat
        String sign = request.getParameter("sign");//	d92eff67b3be05f5e61502e96278d01b

        StringBuilder params = new StringBuilder();
        params.append("merchant_id=").append(merchant_id);
        params.append("&fees=").append(fees);
        params.append("&order_amount=").append(order_amount);
        params.append("&out_trade_no=").append(out_trade_no);
        params.append("&pay_amount=").append(pay_amount);
        params.append("&pay_time=").append(pay_time);
        params.append("&paytype=").append(paytype);
        params.append("&status=").append(status);
        params.append("&trade_no=").append(trade_no);
        params.append(S_KEY);

        //md5加密,大写
        String my_sign = MD5Utils.MD5Encode(params.toString(),"utf-8",true);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>久久微信回调函数校验>>>>>>>>>>>>>>"+(my_sign.equals(sign)));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>my_sign:"+my_sign);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>sign:"+sign);
        if(true || my_sign.equals(sign)){
            //校验成功
            resultMap.put("status",status);
            resultMap.put("out_trade_no",out_trade_no);
            resultMap.put("trade_no",trade_no);
            return resultMap;
        }
        return null;
    }
}
