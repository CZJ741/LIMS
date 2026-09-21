package com.lims.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lims.common.exception.BizException;
import com.lims.system.entity.SysPosition;
import com.lims.system.mapper.SysPositionMapper;
import com.lims.system.service.IPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements IPositionService {

    private final SysPositionMapper positionMapper;

    @Override
    public List<SysPosition> listPositions() {
        return positionMapper.selectList(new LambdaQueryWrapper<SysPosition>()
                .eq(SysPosition::getIsDeleted, 0)
                .orderByAsc(SysPosition::getSortOrder));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPosition(SysPosition position) {
        SysPosition exist = positionMapper.selectOne(new LambdaQueryWrapper<SysPosition>()
                .eq(SysPosition::getPosCode, position.getPosCode())
                .eq(SysPosition::getIsDeleted, 0));
        if (exist != null) {
            throw new BizException("职位编码已存在: " + position.getPosCode());
        }
        position.setCreateTime(LocalDateTime.now());
        positionMapper.insert(position);
        return position.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePosition(SysPosition position) {
        position.setUpdateTime(LocalDateTime.now());
        positionMapper.updateById(position);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePosition(Long id) {
        positionMapper.deleteById(id);
    }
}
