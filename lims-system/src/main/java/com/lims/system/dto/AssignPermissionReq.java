package com.lims.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "角色权限分配请求参数")
public class AssignPermissionReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单与按钮权限ID列表")
    private List<Long> menuIds;

    @Schema(description = "每个菜单对应的数据范围 (GLOBAL/DEPARTMENT/PROJECT_GROUP/PERSONAL)")
    private Map<Long, String> dataScopeMap;
}
