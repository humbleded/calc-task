package com.hc.calc.task.service;

import com.hc.calc.task.model.Mpoint;

import java.util.List;

public interface MpointService {
    List<Mpoint> getMpointsByIds(List<String> ids);
}
