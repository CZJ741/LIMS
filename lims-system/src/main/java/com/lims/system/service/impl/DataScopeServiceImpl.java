package com.lims.system.service.impl;

import com.lims.system.context.UserContext;
import com.lims.system.datascope.DataScope;
import com.lims.system.datascope.DataScopeSqlSnippet;
import com.lims.system.datascope.DataScopeType;
import com.lims.system.service.IDataScopeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据范围 SQL 动态拼接实现
 */
@Service
public class DataScopeServiceImpl implements IDataScopeService {

    @Override
    public DataScopeSqlSnippet buildDataScopeSql(DataScope dataScope, UserContext userContext) {
        if (userContext == null || userContext.isSuperAdmin()) {
            return DataScopeSqlSnippet.empty();
        }

        DataScopeType scopeType = dataScope.scopeType();
        String alias = (dataScope.tableAlias() != null && !dataScope.tableAlias().trim().isEmpty())
                ? dataScope.tableAlias().trim() + "." : "";

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        switch (scopeType) {
            case GLOBAL:
                return DataScopeSqlSnippet.empty();

            case DEPARTMENT:
                if (userContext.getOrgId() != null) {
                    sql.append(" AND ").append(alias).append(dataScope.orgIdColumn()).append(" = ?");
                    params.add(userContext.getOrgId());
                } else {
                    sql.append(" AND 1 = 0");
                }
                break;

            case PROJECT_GROUP:
                List<Long> groupIds = userContext.getGroupIds();
                if (groupIds != null && !groupIds.isEmpty()) {
                    sql.append(" AND ").append(alias).append(dataScope.groupIdColumn()).append(" IN (");
                    for (int i = 0; i < groupIds.size(); i++) {
                        sql.append(i == 0 ? "?" : ", ?");
                        params.add(groupIds.get(i));
                    }
                    sql.append(")");
                } else {
                    sql.append(" AND 1 = 0");
                }
                break;

            case PERSONAL:
            default:
                sql.append(" AND ").append(alias).append(dataScope.userIdColumn()).append(" = ?");
                params.add(userContext.getUserId());
                break;
        }

        return new DataScopeSqlSnippet(sql.toString(), params);
    }
}
