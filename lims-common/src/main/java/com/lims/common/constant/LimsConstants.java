package com.lims.common.constant;

/**
 * 业务模块与生命周期常量
 * 覆盖：GB/T 27025 / ISO/IEC 17025 全生命周期六大阶段及19种预置职位身份
 */
public final class LimsConstants {

    private LimsConstants() {
    }

    /**
     * 业务流程六大阶段
     */
    public static final class Stage {
        public static final String CONTRACT = "CONTRACT";       // 1. 合同登记与审核
        public static final String ENTRUST = "ENTRUST";         // 2. 委托下单
        public static final String SAMPLING = "SAMPLING";       // 3. 采样管理
        public static final String DETECTION = "DETECTION";     // 4. 检测分析与复审
        public static final String REPORT = "REPORT";           // 5. 报告编制与签发
        public static final String FINANCE = "FINANCE";         // 6. 财务结算
    }

    /**
     * 19种预置职位身份编码定义
     */
    public static final class Position {
        public static final String TOP_MANAGEMENT = "POS_TOP_MANAGEMENT";             // 1. 管理层
        public static final String QUALITY_MANAGER = "POS_QUALITY_MANAGER";           // 2. 质量负责人
        public static final String TECHNICAL_DIRECTOR = "POS_TECHNICAL_DIRECTOR";     // 3. 技术负责人
        public static final String MARKET_SPECIALIST = "POS_MARKET_SPECIALIST";       // 4. 市场销售专员
        public static final String FINANCE_OFFICER = "POS_FINANCE_OFFICER";           // 5. 财务人员
        public static final String ENTRUST_CLERK = "POS_ENTRUST_CLERK";               // 6. 下单/接样专员
        public static final String SAMPLE_RECEIVER = "POS_SAMPLE_RECEIVER";           // 7. 样品库管员
        public static final String SAMPLING_LEADER = "POS_SAMPLING_LEADER";           // 8. 采样组长
        public static final String SAMPLER = "POS_SAMPLER";                           // 9. 采样员
        public static final String SAMPLING_REVIEWER = "POS_SAMPLING_REVIEWER";       // 10. 采样质控审核员
        public static final String LAB_HEAD = "POS_LAB_HEAD";                         // 11. 检测室主任
        public static final String ANALYST = "POS_ANALYST";                           // 12. 检测实验员
        public static final String LAB_SUPERVISOR = "POS_LAB_SUPERVISOR";             // 13. 检测质控员
        public static final String DATA_REVIEWER = "POS_DATA_REVIEWER";               // 14. 实验数据复审员
        public static final String REPORT_WRITER = "POS_REPORT_WRITER";               // 15. 报告编制员
        public static final String REPORT_AUDITOR = "POS_REPORT_AUDITOR";             // 16. 报告审核员
        public static final String REPORT_SIGNER = "POS_REPORT_SIGNER";               // 17. 报告授权签字人
        public static final String DEVICE_ADMIN = "POS_DEVICE_ADMIN";                 // 18. 设备管理员
        public static final String STANDARD_MATERIAL_ADMIN = "POS_STD_ADMIN";         // 19. 标物/耗材管理员
    }

    /**
     * 设备直连 5 种数据采集接入方式
     */
    public static final class DeviceAccessType {
        public static final String API_WORKSTATION = "API_WORKSTATION"; // 1. API/工作站软件直连
        public static final String CSV = "CSV";                         // 2. CSV文件解析
        public static final String PDF = "PDF";                         // 3. PDF图谱与文本解析
        public static final String TXT = "TXT";                         // 4. TXT纯文本格式化解析
        public static final String SERIAL_PORT = "SERIAL_PORT";         // 5. RS232/RS485串口直连
    }

    /**
     * RBAC 权限授权维度
     */
    public static final class AuthDimension {
        public static final String POSITION = "POSITION";     // 岗位维度
        public static final String USER = "USER";             // 人员个人维度
        public static final String PROJECT_GROUP = "GROUP";   // 项目组/班组维度
    }
}
