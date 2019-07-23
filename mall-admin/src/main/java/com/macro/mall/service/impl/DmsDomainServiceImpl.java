package com.macro.mall.service.impl;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.mapper.DmsDomainSettingMapper;
import com.macro.mall.model.DmsDomainSetting;
import com.macro.mall.service.DmsDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Create by zhuyong on 2019/7/20
 */
@Service
public class DmsDomainServiceImpl implements DmsDomainService {

    @Autowired
    private DmsDomainSettingMapper domainSettingMapper;

    @Override
    public CommonResult getDomainSettingInfo() {
        DmsDomainSetting dmsDomainSetting = domainSettingMapper.selectByPrimaryKey(1l);
        return CommonResult.success(dmsDomainSetting);
    }

    @Transactional
    @Override
    public CommonResult updateMainDomain(String mainDomain, String landingDomain) {
        //todo 检测入口域名是否被封
        //更新入口域名
        int count = 0;
        DmsDomainSetting setting = domainSettingMapper.selectByPrimaryKey(1l);
        if (setting != null) {
            DmsDomainSetting updateSetting = new DmsDomainSetting();
            updateSetting.setMainDomain(mainDomain);
            updateSetting.setLandingDomain(landingDomain);
            updateSetting.setOprationTime(new Date());
            updateSetting.setId(1l);
            count = domainSettingMapper.updateByPrimaryKeySelective(updateSetting);
        } else {
            DmsDomainSetting insertSetting = new DmsDomainSetting();
            insertSetting.setId(1l);
            insertSetting.setMainDomain(mainDomain);
            insertSetting.setLandingDomain(landingDomain);
            insertSetting.setOprationTime(new Date());
            count = domainSettingMapper.insert(insertSetting);
        }
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }



}
