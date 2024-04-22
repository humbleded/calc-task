package com.hc.calc.task.dao;

import com.hc.calc.task.model.Mpoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MpointMapper {
    List<Mpoint> getMpointByIds(@Param("ids") List<String> ids);
}
