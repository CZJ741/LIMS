package com.lims.system.dto;

import com.lims.system.context.UserContext;
import com.lims.system.entity.SysMenu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Schema(description = "当前登录用户信息及权限")
public class CurrentUserResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户上下文基本信息")
    private UserContext user;

    @Schema(description = "拥有的菜单路由树")
    private List<SysMenu> menus;

    @Schema(description = "拥有的按钮权限标识列表")
    private List<String> permissions;

    @Schema(description = "拥有的角色编码列表")
    private List<String> roles;

    @Schema(description = "拥有的岗位编码列表")
    private List<String> positions;
}
