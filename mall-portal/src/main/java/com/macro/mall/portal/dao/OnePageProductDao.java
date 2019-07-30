package com.macro.mall.portal.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OnePageProductDao {

    @Update("update ems_employee_performance set " +
            " visit_times = visit_times + 1 ," +
            " today_visit_times = today_visit_times + 1," +
            " update_time = current_timestamp" +
            " where  employee_id= #{employeeId} and product_id= #{productId}")
    int countVisitTimes(@Param("employeeId") Long employeeId, @Param("productId") Long productId);

    @Update("update ems_employee_performance set " +
            " today_visit_times = 0" +
            " where  employee_id= #{employeeId} and product_id= #{productId}")
    void clearTodayVisitTimes(@Param("employeeId") Long employeeId, @Param("productId") Long productId);

}
