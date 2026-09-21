package com.lims.system.service;

import com.lims.system.entity.SysPosition;

import java.util.List;

public interface IPositionService {

    List<SysPosition> listPositions();

    Long createPosition(SysPosition position);

    void updatePosition(SysPosition position);

    void deletePosition(Long id);
}
