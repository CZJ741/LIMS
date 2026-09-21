package com.lims.system.service;

import com.lims.system.context.UserContext;
import com.lims.system.datascope.DataScope;
import com.lims.system.datascope.DataScopeSqlSnippet;

/**
 * 数据范围 SQL 拼接服务接口
 */
public interface IDataScopeService {

    /**
     * 根据当前用户岗位/项目组与注解规则，生成 SQL WHERE 过滤片段及参数
     */
    DataScopeSqlSnippet buildDataScopeSql(DataScope dataScope, UserContext userContext);
}
