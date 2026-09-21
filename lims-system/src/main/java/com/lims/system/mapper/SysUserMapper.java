package com.lims.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lims.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询用户所有分配的角色编码
     */
    @Select("""
        SELECT r.role_code
        FROM sys_role r
        INNER JOIN sys_user_role ur ON r.id = ur.role_id
        WHERE ur.user_id = #{userId} AND ur.is_deleted = 0 AND r.is_deleted = 0 AND r.status = 1
    """)
    List<String> findRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户岗位编码
     */
    @Select("""
        SELECT p.pos_code
        FROM sys_position p
        INNER JOIN sys_user_position up ON p.id = up.position_id
        WHERE up.user_id = #{userId} AND up.is_deleted = 0 AND p.is_deleted = 0 AND p.status = 1
    """)
    List<String> findPositionCodesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户所属项目组ID列表
     */
    @Select("""
        SELECT upg.group_id
        FROM sys_user_project_group upg
        INNER JOIN sys_project_group pg ON upg.group_id = pg.id
        WHERE upg.user_id = #{userId} AND upg.is_deleted = 0 AND pg.is_deleted = 0 AND pg.status = 1
    """)
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的所有权限标识（包含菜单 perms 与按钮 perm_tag）
     */
    @Select("""
        SELECT DISTINCT m.perms
        FROM sys_menu m
        INNER JOIN sys_role_menu rm ON m.id = rm.menu_id
        INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
        WHERE ur.user_id = #{userId}
          AND ur.is_deleted = 0
          AND rm.is_deleted = 0
          AND m.is_deleted = 0
          AND m.status = 1
          AND m.perms IS NOT NULL AND m.perms != ''
        UNION
        SELECT DISTINCT b.perm_tag
        FROM sys_button b
        INNER JOIN sys_menu m ON b.menu_id = m.id
        INNER JOIN sys_role_menu rm ON m.id = rm.menu_id
        INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
        WHERE ur.user_id = #{userId}
          AND ur.is_deleted = 0
          AND rm.is_deleted = 0
          AND b.is_deleted = 0
          AND b.status = 1
          AND b.perm_tag IS NOT NULL AND b.perm_tag != ''
    """)
    List<String> findUserPermissions(@Param("userId") Long userId);

    /**
     * 查询用户在各个菜单上的数据范围定义
     */
    @Select("""
        SELECT DISTINCT rm.data_scope
        FROM sys_role_menu rm
        INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
        WHERE ur.user_id = #{userId} AND ur.is_deleted = 0 AND rm.is_deleted = 0
    """)
    List<String> findUserDataScopes(@Param("userId") Long userId);
}
