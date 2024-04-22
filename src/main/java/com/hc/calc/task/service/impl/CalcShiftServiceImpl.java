package com.hc.calc.task.service.impl;

import com.hc.calc.task.dao.CalcShiftMapper;
import com.hc.calc.task.model.CalcShift;
import com.hc.calc.task.service.CalcShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CalcShiftServiceImpl implements CalcShiftService {

    @Autowired
    CalcShiftMapper calcShiftMapper;

    @Override
    public int save(CalcShift calcShift) {
        return calcShiftMapper.save(calcShift);
    }

    @Override
    public int deleteOldLog(Long mpoint, Date startDT) {
        return calcShiftMapper.deleteOldLog(mpoint,startDT);
    }
}
