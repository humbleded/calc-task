package com.hc.calc.task.service.impl;

import com.hc.calc.task.dao.MpointMapper;
import com.hc.calc.task.model.Mpoint;
import com.hc.calc.task.service.MpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MopintServiveImpl implements MpointService {

    @Autowired
    MpointMapper mpointMapper;

    @Override
    public List<Mpoint> getMpointsByIds(List<String> ids) {
        return mpointMapper.getMpointByIds(ids);
    }
}
