package com.macro.mall.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.EmsEmployeeParam;
import com.macro.mall.dto.EmsEmployeeQueryParam;
import com.macro.mall.model.EmsEmployee;

import java.util.List;

/**
 * 员工管理Service
 * Create by zhuyong on 2019/7/13
 */
public interface EmsEmployeeService {

    int create(EmsEmployeeParam employParam);

    List<EmsEmployee> list(EmsEmployeeQueryParam queryParam, Integer pageSize, Integer pageNum);

    int delete(Long employeeId);

    CommonResult selectEmployeeByNickNameOrNickName(String loginName, String employParam);

    int updateEmployee(EmsEmployeeParam employeeParam);

    List<EmsEmployee> getGroupMemberList(Long leaderId);

    List<EmsEmployee> getLeaderList();

    EmsEmployee getUpdateInfo(Long id);
}
