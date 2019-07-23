package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.EmsEmployeeParam;
import com.macro.mall.dto.EmsEmployeeQueryParam;
import com.macro.mall.model.EmsEmployee;
import com.macro.mall.service.EmsEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工管理Controller
 * Create by zhuyong on 2019/7/13
 */
@Controller
@Api(tags = "EmsEmployeeController", description = "员工管理")
@RequestMapping("/employee")
public class EmsEmployeeController {

    @Autowired
    private EmsEmployeeService employeeService;

    @ApiOperation("添加员工")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody EmsEmployeeParam employParam) {
        CommonResult result = employeeService
                .selectEmployeeByNickNameOrNickName(employParam.getLoginName(),employParam.getNickName());
        if(result != null){
            return result;
        }
        int count = employeeService.create(employParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("查询员工列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<EmsEmployee>> list(EmsEmployeeQueryParam queryParam,
                                                      @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<EmsEmployee> employeeList = employeeService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(employeeList));
    }

    @ApiOperation("删除员工")
    @RequestMapping(value = "/delete/{employeeId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult delete(@PathVariable Long employeeId) {
        int count = employeeService.delete(employeeId);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("编辑员工")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@RequestBody EmsEmployeeParam employeeParam) {
        int count = employeeService.updateEmployee(employeeParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取员工编辑信息")
    @RequestMapping(value = "/getUpdateInfo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<EmsEmployee> getUpdateInfo(@PathVariable Long id) {
        return CommonResult.success(employeeService.getUpdateInfo(id));
    }

    @ApiOperation("获取组员列表")
    @RequestMapping(value = "/getGroupMemberList", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult getGroupMemberList(@RequestParam("leaderId") Long leaderId) {
        List<EmsEmployee> employeeList= employeeService.getGroupMemberList(leaderId);
        return CommonResult.success(employeeList);
    }

    @ApiOperation("获取组长信息")
    @RequestMapping(value = "/getLeaderList", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult getLeaderList() {
        List<EmsEmployee> employeeList= employeeService.getLeaderList();
        return CommonResult.success(employeeList);
    }

}
