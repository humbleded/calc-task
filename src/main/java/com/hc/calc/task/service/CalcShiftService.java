package com.hc.calc.task.service;

import com.hc.calc.task.model.CalcShift;

import java.util.Date;

public interface CalcShiftService {
    int save(CalcShift calcShift);

    int deleteOldLog(Long mpoint, Date startDT);
}
