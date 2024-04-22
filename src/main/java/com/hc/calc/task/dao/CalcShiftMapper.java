package com.hc.calc.task.dao;

import com.hc.calc.task.model.CalcShift;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface CalcShiftMapper {
    int save(CalcShift calcShift);

    int deleteOldLog(@Param("mpoint") Long mpoint,@Param("dataDT") Date startDT);
}
