package com.lims.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lims.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户ID查询菜单列表（去重且按排序号排列）
     */
    @Select("""
        SELECT DISTINCT m.*
        FROM sys_menu m
        INNER JOIN sys_role_menu rm ON m.id = rm.menu_id
        INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
        WHERE ur.user_id = #{userId}
          AND ur.is_deleted = 0
          AND rm.is_deleted = 0
          AND m.is_deleted = 0
          AND m.status = 1
        ORDER BY m.parent_id ASC, m.sort_order ASC
    """)
    List<SysMenu> findMenuByUserId(@Param("userId") Long userId);
}
