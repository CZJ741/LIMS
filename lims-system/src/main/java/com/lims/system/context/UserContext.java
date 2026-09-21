package com.lims.system.context;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 当前用户请求上下文数据载体
 */
@Data
@Builder
public class UserContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String realName;
    private Long orgId;
    private List<String> roles;
    private List<String> positions;
    private List<Long> groupIds;
    private List<String> permissions;
    private List<String> dataScopes;

    public boolean isSuperAdmin() {
        return (roles != null && roles.contains("SUPER_ADMIN")) || Long.valueOf(1L).equals(userId);
    }
}
