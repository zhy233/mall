package com.macro.mall.mapper;

import com.macro.mall.model.DmsDomainSetting;
import com.macro.mall.model.DmsDomainSettingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DmsDomainSettingMapper {
    long countByExample(DmsDomainSettingExample example);

    int deleteByExample(DmsDomainSettingExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DmsDomainSetting record);

    int insertSelective(DmsDomainSetting record);

    List<DmsDomainSetting> selectByExample(DmsDomainSettingExample example);

    DmsDomainSetting selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DmsDomainSetting record, @Param("example") DmsDomainSettingExample example);

    int updateByExample(@Param("record") DmsDomainSetting record, @Param("example") DmsDomainSettingExample example);

    int updateByPrimaryKeySelective(DmsDomainSetting record);

    int updateByPrimaryKey(DmsDomainSetting record);
}