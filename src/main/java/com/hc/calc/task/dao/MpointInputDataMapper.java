package com.hc.calc.task.dao;

import com.hc.calc.task.model.BaseData;
import com.hc.calc.task.model.MpointInputData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface MpointInputDataMapper {

    int insertSelective(MpointInputData record);

    Double sum(@Param("id") long id, @Param("start") Date start, @Param("end") Date end);

    Double avg(@Param("id") long id, @Param("start") Date start, @Param("end") Date end);

    Double max(@Param("id") long id, @Param("start") Date start, @Param("end") Date end);

    Double min(@Param("id") long id, @Param("start") Date start, @Param("end") Date end);

    Double prev(@Param("id") long id, @Param("start") Date dataDt);

    List<BaseData> getData(@Param("id") long id, @Param("start") Date start, @Param("end") Date end);

    int addData(MpointInputData mpointInputData);

    Long count(@Param("id") long id, @Param("start") Date start, @Param("end") Date end);

    List<BaseData> getCycleAvgList(@Param("id") long id, @Param("start") Date start, @Param("end") Date end);

    BaseData getBaseDataByTaskIdAndTime(@Param("mpointId")long mpointId,@Param("startDT") Date startDT);

    List<BaseData> prevs(@Param("notAutoMpointIds") List<Long> notAutoMpointIds,@Param("startDT")  Date startDT);

    BaseData getOneCalcHistoryLogBeforeTime(@Param("mpointId") Long mpointId,@Param("startDT") Date startDT);

    List<BaseData> getDataNew(@Param("id") long id, @Param("start") Date start, @Param("end") Date end);
}