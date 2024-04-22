package com.hc.calc.task.service;

import com.hc.calc.task.model.CalcLog;
import com.hc.calc.task.model.CalcRoll;

import java.util.Date;

/**
 * Created by Holger on 2018/3/29.
 */
public interface CalcLogService {
    
    int saveLog(CalcLog calcLog);

    int updateRoll(CalcRoll roll);

    void deleteOldLog(Date datadt, Long taskid);

    CalcLog selectById(long taskId,Date startDT);
}
