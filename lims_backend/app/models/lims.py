from datetime import datetime
from sqlalchemy import Column, Integer, String, DateTime, Text, Float, ForeignKey
from sqlalchemy.orm import relationship
from app.core.database import Base

class SysUser(Base):
    __tablename__ = "sys_user"

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    username = Column(String(50), unique=True, index=True, nullable=False)
    password = Column(String(100), nullable=False)
    real_name = Column(String(50), nullable=True)
    dept_id = Column(Integer, nullable=True)
    dept_name = Column(String(100), nullable=True)
    status = Column(Integer, default=1)  # 1: 正常, 0: 停用
    mobile = Column(String(20), nullable=True)
    email = Column(String(100), nullable=True)
    create_time = Column(DateTime, default=datetime.utcnow)

class SysMenu(Base):
    __tablename__ = "sys_menu"

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    parent_id = Column(Integer, default=0)
    name = Column(String(100), nullable=False)
    url = Column(String(200), nullable=True)
    icon = Column(String(50), nullable=True)
    sort = Column(Integer, default=0)
    type = Column(Integer, default=0)  # 0: 菜单, 1: 按钮

# 业务表：委托单 (Entrust Task)
class LimsEntrustTask(Base):
    __tablename__ = "lims_entrust_task"

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    task_code = Column(String(64), unique=True, index=True, nullable=False) # 任务单号 (WT-2026...)

    # 1. 委托单位信息
    client_name = Column(String(150), nullable=False)                       # 委托单位
    client_address = Column(String(250), nullable=True)                      # 委托单位地址
    client_fax = Column(String(50), nullable=True)                          # 传真
    contact_person = Column(String(50), nullable=True)                      # 联系人
    contact_phone = Column(String(50), nullable=True)                       # 联系电话 / 手机
    client_tel = Column(String(50), nullable=True)                          # 固定电话
    client_email = Column(String(100), nullable=True)                       # 邮箱

    # 2. 受检单位信息
    tested_company = Column(String(150), nullable=True)                      # 受检单位
    tested_address = Column(String(250), nullable=True)                      # 受检地址
    tested_contact = Column(String(50), nullable=True)                      # 受检联系人
    tested_phone = Column(String(50), nullable=True)                        # 受检联系电话

    # 3. 业务与合同信息
    project_name = Column(String(150), nullable=False)                      # 项目名称
    detection_type = Column(String(50), default="环境检测")                 # 检测类型
    seal_type = Column(String(50), default="CMA资质印章")                    # 用章类型
    contract_code = Column(String(64), nullable=True)                       # 所属合同编号
    contract_name = Column(String(200), nullable=True)                      # 所属合同名称
    contract_id = Column(Integer, nullable=True)                            # 关联合同ID
    service_type = Column(String(50), default="委托检测")                   # 服务类型
    salesman = Column(String(50), default="超级管理员")                     # 业务员
    is_subcontract = Column(String(10), default="否")                       # 分包 (是/否)
    sample_delivery_method = Column(String(50), default="自送样")           # 来样方式
    sample_disposal = Column(String(50), default="实验室规范处置留样")       # 余样处置

    # 4. 状态与进度
    status = Column(String(30), default="待下单")                           # 待下单/待采样/待收样/检测中/已完成
    report_deadline = Column(String(50), nullable=True)                     # 应出报告日期

    # 5. 财务与开票
    payment_company = Column(String(150), nullable=True)                    # 付款单位
    amount = Column(Float, default=0.0)                                     # 委托金额
    paid_amount = Column(Float, default=0.0)                                # 已收金额
    discount = Column(Float, default=100.0)                                 # 折扣(%)
    invoice_type = Column(String(50), default="增值税专用发票(6%)")         # 发票类型
    has_quotation = Column(String(20), default="有报价单")                  # 类型有无报价单

    # 6. 报告要求
    report_type = Column(String(50), default="中文正式报告(带CMA章)")        # 报告类型
    report_count = Column(Integer, default=2)                                # 报告份数
    report_fetch_method = Column(String(50), default="顺丰快递到付")         # 取报告方式
    recipient_name = Column(String(50), nullable=True)                       # 报告收件人
    recipient_phone = Column(String(50), nullable=True)                      # 收件人电话
    report_fetch_address = Column(String(250), nullable=True)                # 送达地址

    # 7. 监测方案与技术指标 (JSON文本存储监测方案列表及检测项目)
    monitoring_schemes = Column(Text, nullable=True)
    remark = Column(Text, nullable=True)
    creator = Column(String(50), default="超级管理员")
    create_time = Column(DateTime, default=datetime.utcnow)



# 业务表：样品信息 (Sample)
class LimsSample(Base):
    __tablename__ = "lims_sample"

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    sample_code = Column(String(64), unique=True, index=True, nullable=False) # 样品编号
    sample_name = Column(String(100), nullable=False)                         # 样品名称
    entrust_id = Column(Integer, ForeignKey("lims_entrust_task.id"), nullable=True)
    entrust_code = Column(String(64), nullable=True)
    sample_type = Column(String(50), nullable=True)                           # 固废/水质/土壤/环境空气
    storage_condition = Column(String(100), default="常温密闭")
    status = Column(String(30), default="已登记")                             # 已登记/已入库/已领用/检测中/已归还/已处置
    sampling_date = Column(DateTime, default=datetime.utcnow)
    create_time = Column(DateTime, default=datetime.utcnow)

# 业务表：检测任务与结果 (Detection Task)
class LimsDetectionTask(Base):
    __tablename__ = "lims_detection_task"

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    detection_code = Column(String(64), unique=True, index=True, nullable=False)
    sample_id = Column(Integer, ForeignKey("lims_sample.id"), nullable=True)
    sample_code = Column(String(64), nullable=True)
    sample_name = Column(String(100), nullable=True)
    item_name = Column(String(100), nullable=False)                           # 检测项目 (如 COD, 氨氮, 重金属等)
    method_name = Column(String(150), nullable=True)                          # 检测标准方法 (如 GB/T 11901-1989)
    result_val = Column(String(50), nullable=True)                            # 检测结果值
    unit = Column(String(30), default="mg/L")                                 # 单位
    inspector = Column(String(50), default="admin")                           # 检测人员
    reviewer = Column(String(50), nullable=True)                              # 复审人员
    status = Column(String(30), default="待检测")                             # 待检测/检测中/待复审/已审核
    complete_time = Column(DateTime, nullable=True)
    create_time = Column(DateTime, default=datetime.utcnow)

# 业务表：检测报告 (Report)
class LimsReport(Base):
    __tablename__ = "lims_report"

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    report_code = Column(String(64), unique=True, index=True, nullable=False)
    entrust_code = Column(String(64), nullable=True)
    report_title = Column(String(200), nullable=False)
    client_name = Column(String(150), nullable=False)
    issuer = Column(String(50), default="admin")                              # 签发人
    status = Column(String(30), default="编制中")                             # 编制中/待签发/已签发/已归档
    issue_date = Column(DateTime, nullable=True)
    create_time = Column(DateTime, default=datetime.utcnow)

# 业务表：合同/委托协议 (Contract)
class LimsContract(Base):
    __tablename__ = "lims_contract"

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)

    # 1. 委托单位信息 (委托单位 地址 传真 联系人 邮箱 联系电话 固定电话)
    client_name = Column(String(150), nullable=False)                         # 委托单位
    client_address = Column(String(250), nullable=True)                       # 委托单位地址
    client_fax = Column(String(50), nullable=True)                           # 委托单位传真
    client_contact = Column(String(50), nullable=True)                       # 委托单位联系人
    client_email = Column(String(100), nullable=True)                        # 委托单位邮箱
    client_phone = Column(String(50), nullable=True)                         # 委托单位联系电话
    client_tel = Column(String(50), nullable=True)                           # 委托单位固定电话

    # 2. 受检单位信息 (受检单位 地址 传真 联系人 邮箱 联系电话 固定电话)
    tested_company = Column(String(150), nullable=True)                       # 受检单位
    tested_address = Column(String(250), nullable=True)                       # 受检单位地址
    tested_fax = Column(String(50), nullable=True)                           # 受检单位传真
    tested_contact = Column(String(50), nullable=True)                       # 受检单位联系人
    tested_email = Column(String(100), nullable=True)                        # 受检单位邮箱
    tested_phone = Column(String(50), nullable=True)                         # 受检单位联系电话
    tested_tel = Column(String(50), nullable=True)                           # 受检单位固定电话

    # 3. 合同信息 (合同编号、合同名称 业务类型 检测类型 用章类型 服务类型 签订日期 开始日期 结束日期 来样方式 接受分包 是否判定 使用非标方法 业务员 余样处置 折扣 合同金额 备注 附件)
    contract_code = Column(String(64), nullable=True, index=True)             # 合同编号
    contract_name = Column(String(200), nullable=False)                       # 合同名称
    biz_type = Column(String(50), default="环境污染(HW)")                     # 业务类型
    detection_type = Column(String(50), default="环境检测")                   # 检测类型
    seal_type = Column(String(50), default="CMA资质印章")                     # 用章类型
    service_type = Column(String(50), default="委托检测")                     # 服务类型
    sign_date = Column(String(50), nullable=True)                             # 签订日期
    start_date = Column(String(50), nullable=True)                            # 开始日期
    end_date = Column(String(50), nullable=True)                              # 结束日期
    sample_delivery_method = Column(String(50), default="自送样")            # 来样方式
    accept_subcontract = Column(String(20), default="是")                     # 接受分包
    is_judge = Column(String(20), default="是")                               # 是否判定
    use_non_standard_method = Column(String(20), default="否")                # 使用非标方法
    salesman = Column(String(50), default="业务经理-张伟")                    # 业务员
    sample_disposal = Column(String(50), default="实验室统一留样销毁")        # 余样处置
    discount = Column(Float, default=100.0)                                   # 折扣(%)
    contract_amount = Column(Float, default=0.0)                              # 合同金额
    total_amount_excl_tax = Column(Float, default=0.0)                        # 总价(不含税)
    quotation_code = Column(String(64), nullable=True)                        # 报价编号
    preset_approver = Column(String(50), default="技术主管-李工")             # 预设审批人
    order_cs = Column(String(50), default="客服-王敏")                        # 下单客服
    remark = Column(Text, nullable=True)                                      # 备注
    attachment_name = Column(String(200), nullable=True)                      # 附件

    # 4. 报告要求 (一单一库 报告类型 报告要求 报告数量 取报告方式 取报告单位 取报告地址 收件人 收件人电话)
    one_order_one_pool = Column(String(20), default="是")                     # 一单一库
    report_type = Column(String(50), default="中文正式报告(带CMA章)")         # 报告类型
    report_requirement = Column(String(100), default="常规出具")              # 报告要求
    report_count = Column(Integer, default=2)                                 # 报告数量
    report_fetch_method = Column(String(50), default="顺丰快递到付")          # 取报告方式
    report_fetch_company = Column(String(150), nullable=True)                 # 取报告单位
    report_fetch_address = Column(String(250), nullable=True)                 # 取报告地址
    recipient_name = Column(String(50), nullable=True)                        # 收件人
    recipient_phone = Column(String(50), nullable=True)                       # 收件人电话

    # 5. 付款信息 (发票类型 发票抬头 纳税人识别号 发票地址 发票电话 银行名称 银行账号)
    invoice_type = Column(String(50), default="增值税专用发票(6%)")          # 发票类型
    invoice_title = Column(String(150), nullable=True)                        # 发票抬头
    taxpayer_id = Column(String(100), nullable=True)                          # 纳税人识别号
    invoice_address = Column(String(250), nullable=True)                      # 发票地址
    invoice_phone = Column(String(50), nullable=True)                         # 发票电话
    bank_name = Column(String(100), nullable=True)                           # 银行名称
    bank_account = Column(String(100), nullable=True)                         # 银行账号

    # 6. 监测方案数据 (JSON文本存储监测方案列表及每个方案绑定的检测项目)
    monitoring_schemes = Column(Text, nullable=True)

    status = Column(String(30), default="已生效")                             # 状态：草稿/审批中/已生效/已归档
    create_time = Column(DateTime, default=datetime.utcnow)

# 业务表：通用工作流审批任务表 (Audit Task)
class LimsAuditTask(Base):
    __tablename__ = "lims_audit_task"

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    business_code = Column(String(64), unique=True, index=True, nullable=False)   # 业务编号 (如报价单号 BJ-2026... 或合同编号 HT-...)
    business_type = Column(String(50), default="报价审批")                        # 业务类型 (报价审批/合同审批/报告审核等)
    business_name = Column(String(200), nullable=False)                         # 业务名称
    applicant = Column(String(50), nullable=False, default="业务部-张伟")         # 申报人
    applicant_dept = Column(String(100), default="市场业务部")                   # 申报部门
    current_node = Column(String(50), default="报价审核")                        # 当前节点 (报价审核/合同会签/中心主任审批等)
    current_handler = Column(String(50), default="技术主管-李工")                 # 当前处理人
    is_finished = Column(String(10), default="否")                              # 是否结束：是 / 否
    audit_result = Column(String(30), default="待审核")                          # 审批结果：待审核 / 通过 / 驳回
    audit_comment = Column(Text, nullable=True)                                 # 审批批注/意见
    contract_id = Column(Integer, nullable=True)                                # 关联合同/报价ID
    attachment_info = Column(Text, nullable=True)                               # 附件信息(JSON存储)
    audit_history = Column(Text, nullable=True)                                 # 审批流转历史记录(JSON存储节点操作人/时间/结果/意见)
    apply_time = Column(DateTime, default=datetime.utcnow)                      # 申报提交时间
    audit_time = Column(DateTime, nullable=True)                                # 审核处理时间




