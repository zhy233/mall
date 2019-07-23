package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dao.EmsEmployeeDao;
import com.macro.mall.dto.EmsEmployeeParam;
import com.macro.mall.dto.EmsEmployeeQueryParam;
import com.macro.mall.mapper.EmsEmployeeMapper;
import com.macro.mall.model.EmsEmployee;
import com.macro.mall.model.EmsEmployeeExample;
import com.macro.mall.service.EmsEmployeeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 员工管理Service实现类
 * Create by zhuyong on 2019/7/13
 */
@Service
public class EmsEmployeeServiceImpl implements EmsEmployeeService {

    @Autowired
    private EmsEmployeeMapper employeeMapper;

    @Autowired
    private EmsEmployeeDao employeeDao;

    @Override
    public int create(EmsEmployeeParam employParam) {
        EmsEmployee employee = new EmsEmployee();
        employee.setLoginName(employParam.getLoginName());
        employee.setNickName(employParam.getNickName());
        employee.setEmployeeLevel(employParam.getLevel());
        employee.setGroupLeaderId(employParam.getLeaderId());
        employee.setCreateTime(new Date());
        employee.setStatus(0);
        return employeeMapper.insert(employee);
    }

    @Override
    public List<EmsEmployee> list(EmsEmployeeQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        EmsEmployeeExample example = new EmsEmployeeExample();
//        example.createCriteria().andStatusEqualTo(0);
        example.setOrderByClause("create_time desc");
        return employeeMapper.selectByExample(example);
    }

    @Override
    public int delete(Long employeeId) {
        //逻辑删除
        EmsEmployee employee = new EmsEmployee();
        employee.setId(employeeId);
        employee.setStatus(1);
        return employeeMapper.updateByPrimaryKeySelective(employee);
    }

    @Override
    public CommonResult selectEmployeeByNickNameOrNickName(String loginName, String nickName) {
        EmsEmployeeExample example = new EmsEmployeeExample();
        example.createCriteria().andLoginNameEqualTo(loginName);
        List<EmsEmployee> employees1 = employeeMapper.selectByExample(example);

        String message = "";
        if(CollectionUtils.isNotEmpty(employees1)){
           message = "登录名已存在";
        }else{
            example.clear();
            example.createCriteria().andNickNameEqualTo(nickName);
            List<EmsEmployee> employees2 = employeeMapper.selectByExample(example);
            if(CollectionUtils.isEmpty(employees2)){
                return null;
            }
            message = "昵称已存在";
        }

        return CommonResult.failed(message);
    }

    @Override
    public int updateEmployee(EmsEmployeeParam employeeParam) {
        if(employeeParam.getEmployeeId() != null){
            EmsEmployee record = new EmsEmployee();
            record.setId(employeeParam.getEmployeeId());
            record.setNickName(employeeParam.getNickName());
            record.setLoginName(employeeParam.getLoginName());
            record.setGroupLeaderId(employeeParam.getLeaderId());
            record.setEmployeeLevel(employeeParam.getLevel());

            return employeeMapper.updateByPrimaryKeySelective(record);
        }
        return 0;
    }

    @Override
    public List<EmsEmployee> getGroupMemberList(Long leaderId) {
        EmsEmployeeExample example = new EmsEmployeeExample();
        example.createCriteria().andGroupLeaderIdEqualTo(leaderId);
        example.createCriteria().andStatusEqualTo(0);

        return employeeMapper.selectByExample(example);
    }

    @Override
    public List<EmsEmployee> getLeaderList() {
        EmsEmployeeExample example = new EmsEmployeeExample();
        example.createCriteria().andEmployeeLevelEqualTo(2);
        example.createCriteria().andStatusEqualTo(0);

        return employeeMapper.selectByExample(example);
    }

    @Override
    public EmsEmployee getUpdateInfo(Long id) {
        return employeeMapper.selectByPrimaryKey(id);
    }
}
