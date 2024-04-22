package com.hc.calc.task.service.impl;

import com.hc.calc.task.dao.CalcLogMapper;
import com.hc.calc.task.model.CalcLog;
import com.hc.calc.task.model.CalcRoll;
import com.hc.calc.task.service.CalcLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Administrator on 2018/3/29.
 */
@Service("calcLogService")
public class CalcLogServiceImpl implements CalcLogService {

    @Autowired
    private CalcLogMapper mapper;
    
    @Override 
    public int saveLog(CalcLog calcLog) {
	return mapper.saveLog(calcLog);
    }

    @Override 
    public int updateRoll(CalcRoll roll) {
	return mapper.updateRoll(roll);
    }

    @Override
    public void deleteOldLog(Date datadt, Long taskid) {
        mapper.deleteOldLog(datadt,taskid);
    }

    @Override
    public CalcLog selectById(long taskId,Date startDT) {
        return mapper.selectByTaskId(taskId,startDT);
    }
}
