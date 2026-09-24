from typing import Optional, List, Any
from datetime import datetime
from pydantic import BaseModel

class StandardResponse(BaseModel):
    code: int = 0
    msg: str = "success"
    data: Optional[Any] = None

class LoginRequest(BaseModel):
    username: str
    password: str
    uuid: Optional[str] = ""
    captcha: Optional[str] = ""

# 委托单
class EntrustCreate(BaseModel):
    # 1. 委托单位
    client_name: str
    client_address: Optional[str] = ""
    client_fax: Optional[str] = ""
    contact_person: Optional[str] = ""
    contact_phone: Optional[str] = ""
    client_tel: Optional[str] = ""
    client_email: Optional[str] = ""

    # 2. 受检单位
    tested_company: Optional[str] = ""
    tested_address: Optional[str] = ""
    tested_contact: Optional[str] = ""
    tested_phone: Optional[str] = ""

    # 3. 业务与合同
    project_name: str
    detection_type: Optional[str] = "环境检测"
    seal_type: Optional[str] = "CMA资质印章"
    contract_code: Optional[str] = ""
    contract_name: Optional[str] = ""
    contract_id: Optional[int] = None
    service_type: Optional[str] = "委托检测"
    salesman: Optional[str] = "超级管理员"
    is_subcontract: Optional[str] = "否"
    sample_delivery_method: Optional[str] = "自送样"
    sample_disposal: Optional[str] = "实验室规范处置留样"

    # 4. 状态与进度
    status: Optional[str] = "待下单"
    report_deadline: Optional[str] = ""

    # 5. 财务与开票
    payment_company: Optional[str] = ""
    amount: Optional[float] = 0.0
    paid_amount: Optional[float] = 0.0
    discount: Optional[float] = 100.0
    invoice_type: Optional[str] = "增值税专用发票(6%)"
    has_quotation: Optional[str] = "有报价单"

    # 6. 报告要求
    report_type: Optional[str] = "中文正式报告(带CMA章)"
    report_count: Optional[int] = 2
    report_fetch_method: Optional[str] = "顺丰快递到付"
    recipient_name: Optional[str] = ""
    recipient_phone: Optional[str] = ""
    report_fetch_address: Optional[str] = ""

    # 7. 监测方案与技术指标
    monitoring_schemes: Optional[Any] = None
    remark: Optional[str] = ""

class EntrustOut(EntrustCreate):
    id: int
    task_code: str
    status: str
    creator: str
    create_time: datetime
    class Config:
        from_attributes = True



# 样品
class SampleCreate(BaseModel):
    sample_name: str
    sample_type: str
    entrust_code: Optional[str] = ""
    storage_condition: Optional[str] = "常温密闭"

class SampleOut(SampleCreate):
    id: int
    sample_code: str
    status: str
    create_time: datetime
    class Config:
        from_attributes = True

# 检测
class DetectionCreate(BaseModel):
    sample_code: str
    sample_name: str
    item_name: str
    method_name: Optional[str] = "GB/T 标准方法"
    unit: Optional[str] = "mg/L"

class DetectionUpdateResult(BaseModel):
    result_val: str
    status: Optional[str] = "待复审"

class DetectionOut(BaseModel):
    id: int
    detection_code: str
    sample_code: Optional[str]
    sample_name: Optional[str]
    item_name: str
    method_name: Optional[str]
    result_val: Optional[str]
    unit: Optional[str]
    inspector: Optional[str]
    reviewer: Optional[str]
    status: str
    create_time: datetime
    class Config:
        from_attributes = True

# 报告
class ReportCreate(BaseModel):
    report_title: str
    client_name: str
    entrust_code: Optional[str] = ""

class ReportOut(ReportCreate):
    id: int
    report_code: str
    issuer: str
    status: str
    create_time: datetime
    class Config:
        from_attributes = True

# 合同/委托协议
class ContractCreate(BaseModel):
    # 1. 委托单位信息
    client_name: str
    client_address: Optional[str] = ""
    client_fax: Optional[str] = ""
    client_contact: Optional[str] = ""
    client_email: Optional[str] = ""
    client_phone: Optional[str] = ""
    client_tel: Optional[str] = ""

    # 2. 受检单位信息
    tested_company: Optional[str] = ""
    tested_address: Optional[str] = ""
    tested_fax: Optional[str] = ""
    tested_contact: Optional[str] = ""
    tested_email: Optional[str] = ""
    tested_phone: Optional[str] = ""
    tested_tel: Optional[str] = ""

    # 3. 合同信息
    contract_code: Optional[str] = ""
    contract_name: str
    biz_type: Optional[str] = "环境污染(HW)"
    detection_type: Optional[str] = "环境检测"
    seal_type: Optional[str] = "CMA资质印章"
    service_type: Optional[str] = "委托检测"
    sign_date: Optional[str] = ""
    start_date: Optional[str] = ""
    end_date: Optional[str] = ""
    sample_delivery_method: Optional[str] = "自送样"
    accept_subcontract: Optional[str] = "是"
    is_judge: Optional[str] = "是"
    use_non_standard_method: Optional[str] = "否"
    salesman: Optional[str] = "业务经理-张伟"
    sample_disposal: Optional[str] = "实验室统一留样销毁"
    discount: Optional[float] = 100.0
    contract_amount: Optional[float] = 0.0
    total_amount_excl_tax: Optional[float] = 0.0
    quotation_code: Optional[str] = ""
    preset_approver: Optional[str] = "技术主管-李工"
    order_cs: Optional[str] = "客服-王敏"
    remark: Optional[str] = ""
    attachment_name: Optional[str] = ""

    # 4. 报告要求
    one_order_one_pool: Optional[str] = "是"
    report_type: Optional[str] = "中文正式报告(带CMA章)"
    report_requirement: Optional[str] = "常规出具"
    report_count: Optional[int] = 2
    report_fetch_method: Optional[str] = "顺丰快递到付"
    report_fetch_company: Optional[str] = ""
    report_fetch_address: Optional[str] = ""
    recipient_name: Optional[str] = ""
    recipient_phone: Optional[str] = ""

    # 5. 付款信息
    invoice_type: Optional[str] = "增值税专用发票(6%)"
    invoice_title: Optional[str] = ""
    taxpayer_id: Optional[str] = ""
    invoice_address: Optional[str] = ""
    invoice_phone: Optional[str] = ""
    bank_name: Optional[str] = ""
    bank_account: Optional[str] = ""

    # 6. 监测方案数据 (JSON字符串或列表对象)
    monitoring_schemes: Optional[Any] = None

    status: Optional[str] = "已生效"

class ContractOut(ContractCreate):
    id: int
    create_time: datetime
    class Config:
        from_attributes = True

# 审批任务模型
class AuditTaskCreate(BaseModel):
    business_code: str
    business_type: Optional[str] = "报价审批"
    business_name: str
    applicant: Optional[str] = "业务部-张伟"
    applicant_dept: Optional[str] = "市场业务部"
    current_node: Optional[str] = "报价审核"
    current_handler: Optional[str] = "技术主管-李工"
    is_finished: Optional[str] = "否"
    audit_result: Optional[str] = "待审核"
    audit_comment: Optional[str] = ""
    contract_id: Optional[int] = None
    attachment_info: Optional[str] = ""
    audit_history: Optional[str] = ""

class AuditTaskApprove(BaseModel):
    action: str  # 同意 / 驳回 / 回退
    audit_comment: Optional[str] = "同意"
    handler: Optional[str] = "超级管理员"
    contract_data: Optional[Any] = None  # 支持在合同录入节点同时提交修改后的合同细节及监测方案


class AuditTaskOut(AuditTaskCreate):
    id: int
    apply_time: datetime
    audit_time: Optional[datetime] = None
    class Config:
        from_attributes = True



