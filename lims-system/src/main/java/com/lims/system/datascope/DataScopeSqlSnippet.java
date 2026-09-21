package com.lims.system.datascope;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态数据范围 SQL 片段封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataScopeSqlSnippet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 拼接后的 SQL 片段 (例如 " AND t.org_id = ? ")
     */
    private String sqlSegment = "";

    /**
     * SQL 参数列表
     */
    private List<Object> params = new ArrayList<>();

    public static DataScopeSqlSnippet empty() {
        return new DataScopeSqlSnippet("", new ArrayList<>());
    }
}
