package com.macro.mall.portal.component;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderTaskLogMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderExample;
import com.macro.mall.model.OmsOrderTaskLog;
import com.macro.mall.portal.service.OnePageService;
import com.macro.mall.portal.util.HttpClientUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单操作线程
 * Create by zhuyong on 2019/7/27
 */
@Component
public class OrderTaskThread {

    @Value("${is-pay.payId}")
    private Integer PAY_ID;

    private final OnePageService onePageService;

    @Autowired
    public OrderTaskThread( OnePageService onePageService) {
        this.onePageService = onePageService;
    }
//
//    @Async
//    public Future<Map<String,Object>> test1() {
//        Map<String,Object> result=new HashMap<String,Object>();
//        return new AsyncResult<>(result);
//    }

    /**
     * 支付成功，查询本地信息是否被更改，
     * 如果没有，去查询第三方支付是否有记录然后修改订单状态
     * Create by zhuyong on 2019/7/27
     */
    @Async
    public Boolean paySuccess(String orderNumber) {
        try {

            String result = "";
            Integer count = 0;
            do{
                Thread.sleep(2000);
                result = onePageService.paySuccess2(orderNumber,result);
                count++;
                if(count == 10){
                   onePageService.insertTaskLog(orderNumber);
                }
            }while (StringUtils.isEmpty(result) && count < 10); //查询10次以后还是差不到

            if(!StringUtils.isEmpty(result)){
                JSONObject resultJson = JSONObject.fromObject(result);
                String state = (String) resultJson.get("State");
                if ("success".equals(state)) {
                    CommonResult commonResult = onePageService.paySuccess(orderNumber);
                    if (commonResult.getCode() == 200) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
