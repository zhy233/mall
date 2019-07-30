package com.macro.mall.service.impl;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsProductShortChainResult;
import com.macro.mall.mapper.DmsDomainSettingMapper;
import com.macro.mall.mapper.EmsEmployeeMapper;
import com.macro.mall.mapper.PmsProductShortChainMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.PmsProductShortChainService;
import com.macro.mall.util.HttpClientUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.*;

/**
 * 商品短链接口实现类
 * Create by zhuyong on 2019/7/21
 */
@Service
public class PmsProductShortChainServiceImpl implements PmsProductShortChainService {

    @Autowired
    private DmsDomainSettingMapper domainSettingMapper;

    @Autowired
    private PmsProductShortChainMapper shortChainMapper;

    @Autowired
    private EmsEmployeeMapper employeeMapper;

    @Transactional
    @Override
    public CommonResult createShortChain(Long employeeId, Long productId) {
        DmsDomainSetting setting = domainSettingMapper.selectByPrimaryKey(1l);
        if (setting == null) {
            return CommonResult.failed("请先配置入口域名!");
        }

        EmsEmployee employee = employeeMapper.selectByPrimaryKey(employeeId);
        if(employee == null){
            return CommonResult.failed("该员工不存在");
        }

        //拼接长链
        String text = "http://" + setting.getMainDomain() + "/#/entry"
                + "?employeeId=" + employeeId
                + "&productId=" + productId;

        //查询短链
        PmsProductShortChainExample shortChainExample = new PmsProductShortChainExample();
        shortChainExample.createCriteria().andEmployeeIdEqualTo(employeeId).andProductIdEqualTo(productId);
        List<PmsProductShortChain> pmsProductShortChains =
                shortChainMapper.selectByExample(shortChainExample);
        //存在短链记录
        if (pmsProductShortChains != null && pmsProductShortChains.size() > 0) {
            PmsProductShortChain pmsProductShortChain = pmsProductShortChains.get(0);
            if(text.equals(pmsProductShortChain.getLongChain())){
                //入口域名不变
                return CommonResult.success(pmsProductShortChain);
            }else{}
            //更新短链
            String shortChain = getShortChain(text);
            if(StringUtils.isEmpty(shortChain)) return CommonResult.failed("网络繁忙，请稍后再试");
            PmsProductShortChain updateShortChain = new PmsProductShortChain();
            updateShortChain.setId(pmsProductShortChain.getId());
            updateShortChain.setMainDomain(setting.getMainDomain());
            updateShortChain.setLongChain(text);
            updateShortChain.setShortChain(shortChain);
            shortChainMapper.updateByPrimaryKeySelective(updateShortChain);

        }else{
            //生成短链
            String shortChain = getShortChain(text);
            if(StringUtils.isEmpty(shortChain)) return CommonResult.failed("网络繁忙，请稍后再试");
            PmsProductShortChain insertShortChain = new PmsProductShortChain();
            insertShortChain.setEmployeeId(employeeId);
            insertShortChain.setProductId(productId);
            insertShortChain.setEmployeeName(employee.getLoginName());
            insertShortChain.setMainDomain(setting.getMainDomain());
            insertShortChain.setShortChain(shortChain);
            insertShortChain.setLongChain(text);
            insertShortChain.setUpdateTime(new Date());
            shortChainMapper.insert(insertShortChain);
        }

        //获取短链
        PmsProductShortChainExample resultExample = new PmsProductShortChainExample();
        resultExample.createCriteria().andEmployeeIdEqualTo(employeeId).andProductIdEqualTo(productId);
        List<PmsProductShortChain> shortChains =
                shortChainMapper.selectByExample(shortChainExample);
        if(shortChains != null && shortChains.size() > 0){
            return CommonResult.success(shortChains.get(0));
        }

        return CommonResult.failed("短链生成失败");
    }


    @Override
    public CommonResult getMainDomainUrl(Long employeeId, Long productId) {
        DmsDomainSetting setting = domainSettingMapper.selectByPrimaryKey(1l);
        if (setting == null) {
            return CommonResult.failed("请先配置入口域名!");
        }

        EmsEmployee employee = employeeMapper.selectByPrimaryKey(employeeId);
        if(employee == null){
            return CommonResult.failed("该员工不存在");
        }

        //拼接长链
        String text = "http://" + setting.getMainDomain() + "/goods"
                + "?employeeId=" + employeeId
                + "&productId=" + productId;

        return CommonResult.success(text);
    }


    @Override
    public CommonResult getShortChainList(Long productId) {
        PmsProductShortChainResult result = new PmsProductShortChainResult();

        EmsEmployeeExample example = new EmsEmployeeExample();
        example.createCriteria().andStatusEqualTo(0);
        List<EmsEmployee> employees = employeeMapper.selectByExample(example);

        PmsProductShortChainExample resultExample = new PmsProductShortChainExample();
        resultExample.createCriteria().andProductIdEqualTo(productId);
        List<PmsProductShortChain> shortChains = shortChainMapper.selectByExample(resultExample);

        Map<Long,PmsProductShortChain> shortChainMap = new HashMap<>();
        for(PmsProductShortChain shortChain : shortChains){
            shortChainMap.put(shortChain.getEmployeeId(),shortChain);
        }
        List<PmsProductShortChain> chainList = new ArrayList<>();

        for(EmsEmployee employee : employees){
            PmsProductShortChain shortChain = shortChainMap.get(employee.getId());

            PmsProductShortChain chain = new PmsProductShortChain();
            chain.setEmployeeId(employee.getId());
            chain.setEmployeeName(employee.getNickName());
            chain.setProductId(productId);

            if(shortChain != null){
               chain.setLongChain(shortChain.getLongChain());
               chain.setShortChain(shortChain.getShortChain());
               chain.setMainDomain(shortChain.getMainDomain());
               chain.setUpdateTime(shortChain.getUpdateTime());
               chain.setId(shortChain.getId());
            }
            chainList.add(chain);
        }
        result.setShortChainList(chainList);

        return CommonResult.success(result);
    }

    public String getShortChain(String text){
        System.out.println("==========start get short chain===========");
        try{
//            String params = "&token=28e43f2fc4993d416ac67327aa3514c3&long=" + text;
            Map<Object,Object> params = new HashMap<>();
            params.put("token","28e43f2fc4993d416ac67327aa3514c3");
            params.put("long",URLEncoder.encode(text,"utf-8"));
            //wxdwz
            //String result = HttpClientUtil.doPostForm("http://baofeng.la/index.php?a=addon&m=url2",params, "UTF-8");

            //String result = HttpClientUtil.doPostForm("http://baofeng.la/index.php?a=addon&m=wxdwz",params, "UTF-8");
            String url = "http://baofeng.la/index.php?a=addon&m=wxdwz" +
                    "&token=" +params.get("token")
                    +"&long=" +params.get("long");

            String result = HttpClientUtil.get(url);

            if(StringUtils.isEmpty(result)){
                //调用短码生成接口异常
                System.out.println("=========params:"+ params.toString());
                return "";
            }

            JSONObject jsonObject = JSONObject.fromObject(result);
            System.out.println("========getShortChain:"+result);
            Integer code = (Integer)jsonObject.get("ret_code");
            int ret_code = code != null? code : -1;
            if (ret_code == 0) {
                //获取到短链
                return (String) jsonObject.get("short");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
