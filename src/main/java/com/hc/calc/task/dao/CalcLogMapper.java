package com.hc.calc.task.dao;

import com.hc.calc.task.model.CalcLog;
import com.hc.calc.task.model.CalcRoll;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper 
public interface CalcLogMapper {

    int saveLog(CalcLog calcLog);

    int updateRoll(CalcRoll roll);

    /**
     * @param datadt
     * @param taskid
     */
    void deleteOldLog(@Param("datadt") Date datadt,@Param("taskid") Long taskid);

    CalcLog selectByTaskId(@Param("taskId") long taskId,@Param("startDT") Date startDT);
}
