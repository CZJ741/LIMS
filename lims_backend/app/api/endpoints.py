import json
import os
import random
from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException, Header
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.lims import SysUser, LimsEntrustTask, LimsSample, LimsDetectionTask, LimsReport, LimsContract, LimsAuditTask, LimsSamplingPreparation
from app.schemas.lims import (
    StandardResponse, LoginRequest,
    EntrustCreate, EntrustOut,
    SampleCreate, SampleOut,
    DetectionCreate, DetectionUpdateResult, DetectionOut,
    ReportCreate, ReportOut,
    ContractCreate, ContractOut,
    AuditTaskCreate, AuditTaskApprove, AuditTaskOut,
    SamplingPreparationSave, SamplingPreparationOut
)

router = APIRouter()

# ----------------- 用户与系统导航接口 (精确对接原平台) -----------------
@router.post("/login", response_model=StandardResponse)
def login(form_data: LoginRequest, db: Session = Depends(get_db)):
    # 原平台账号 admin / 密码 123456
    user = db.query(SysUser).filter(SysUser.username == form_data.username).first()
    if not user or user.password != form_data.password:
        return StandardResponse(code=500, msg="账号或密码错误")

    # 成功生成 token
    token = f"lims_token_{random.randint(10000000, 99999999)}"
    return StandardResponse(data={
        "token": token,
        "expire": 43200,
        "username": user.username,
        "staffName": user.real_name or "超级管理员"
    })

@router.get("/ridol/sys/user/info", response_model=StandardResponse)
def get_user_info(db: Session = Depends(get_db)):
    return StandardResponse(data={
        "id": "admin",
        "username": "admin",
        "staffName": "超级管理员",
        "superAdmin": 1,
        "deptName": "技术检测中心",
        "status": 1
    })

@router.get("/ridol/sys/menu/nav", response_model=StandardResponse)
def get_menu_nav():
    # 优先加载抓取到的完整 30 个根业务模块与近 200 个子模块
    nav_file = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))), "menu_nav.json")
    if os.path.exists(nav_file):
        try:
            with open(nav_file, "r", encoding="utf-8") as f:
                data = json.load(f)
                return StandardResponse(data=data.get("data", []))
        except Exception:
            pass

    # 兜底核心业务结构
    fallback_nav = [
        {"name": "任务列表", "icon": "icon-user", "children": [{"name": "我的申请"}, {"name": "我的审批任务"}]},
        {"name": "委托单管理", "icon": "icon-solution", "children": [{"name": "委托下单"}, {"name": "委托单汇总"}]},
        {"name": "采样管理", "icon": "icon-car", "children": [{"name": "采样前准备"}, {"name": "我的采样"}]},
        {"name": "样品流转", "icon": "icon-appstore", "children": [{"name": "样品登记"}, {"name": "样品领用"}]},
        {"name": "检测管理", "icon": "icon-experiment", "children": [{"name": "实验室数据入录"}, {"name": "检测任务汇总"}]},
        {"name": "报告管理", "icon": "icon-file-done", "children": [{"name": "报告编制"}, {"name": "报告审核"}]},
        {"name": "标准品管理", "icon": "icon-gold", "children": [{"name": "标准物质台账"}]},
        {"name": "仪器管理", "icon": "icon-tool", "children": [{"name": "仪器设备台账"}]},
        {"name": "系统管理", "icon": "icon-setting", "children": [{"name": "用户管理"}, {"name": "角色管理"}]}
    ]
    return StandardResponse(data=fallback_nav)

# ----------------- 核心业务流程 1: 委托单管理 -----------------
@router.get("/entrust/list", response_model=StandardResponse)
def list_entrust(
    client_name: str = None,
    task_code: str = None,
    project_name: str = None,
    detection_type: str = None,
    status: str = None,
    salesman: str = None,
    db: Session = Depends(get_db)
):
    query = db.query(LimsEntrustTask)
    if client_name:
        query = query.filter(LimsEntrustTask.client_name.contains(client_name.strip()))
    if task_code:
        query = query.filter(LimsEntrustTask.task_code.contains(task_code.strip()))
    if project_name:
        query = query.filter(LimsEntrustTask.project_name.contains(project_name.strip()))
    if detection_type:
        query = query.filter(LimsEntrustTask.detection_type == detection_type)
    if status:
        query = query.filter(LimsEntrustTask.status == status)
    if salesman:
        query = query.filter(LimsEntrustTask.salesman.contains(salesman.strip()))

    tasks = query.order_by(LimsEntrustTask.id.desc()).all()
    res = []
    for t in tasks:
        t_dict = EntrustOut.from_orm(t).dict()
        if t.monitoring_schemes:
            try:
                t_dict["monitoring_schemes"] = json.loads(t.monitoring_schemes)
            except:
                t_dict["monitoring_schemes"] = []
        else:
            t_dict["monitoring_schemes"] = []
        res.append(t_dict)
    return StandardResponse(data=res)

@router.post("/entrust/create", response_model=StandardResponse)
def create_entrust(entrust: EntrustCreate, db: Session = Depends(get_db)):
    code = f"WT-{datetime.now().strftime('%Y%m%d%H%M%S')}"
    data = entrust.dict()
    if isinstance(data.get("monitoring_schemes"), (list, dict)):
        data["monitoring_schemes"] = json.dumps(data["monitoring_schemes"], ensure_ascii=False)

    new_task = LimsEntrustTask(
        task_code=code,
        creator="超级管理员",
        **data
    )
    db.add(new_task)
    db.commit()
    db.refresh(new_task)
    res_dict = EntrustOut.from_orm(new_task).dict()
    if new_task.monitoring_schemes:
        try:
            res_dict["monitoring_schemes"] = json.loads(new_task.monitoring_schemes)
        except:
            res_dict["monitoring_schemes"] = []
    return StandardResponse(msg="委托单创建成功", data=res_dict)

@router.put("/entrust/update/{entrust_id}", response_model=StandardResponse)
def update_entrust(entrust_id: int, entrust_data: dict, db: Session = Depends(get_db)):
    task = db.query(LimsEntrustTask).filter(LimsEntrustTask.id == entrust_id).first()
    if not task:
        return StandardResponse(code=404, msg="委托任务不存在")

    for k, v in entrust_data.items():
        if hasattr(task, k) and k not in ["id", "task_code", "create_time"]:
            if k == "monitoring_schemes" and isinstance(v, (list, dict)):
                setattr(task, k, json.dumps(v, ensure_ascii=False))
            else:
                setattr(task, k, v)
    db.commit()
    return StandardResponse(msg="委托任务修改更新成功！")

@router.post("/entrust/{entrust_id}/submit-sampling", response_model=StandardResponse)
def submit_sampling(entrust_id: int, db: Session = Depends(get_db)):
    task = db.query(LimsEntrustTask).filter(LimsEntrustTask.id == entrust_id).first()
    if not task:
        return StandardResponse(code=404, msg="委托单不存在")

    # 状态变更为待采样
    task.status = "待采样"

    # 同步自动生成该委托单对应的现场待采样样品记录
    s_code = f"YP-{datetime.now().strftime('%Y%m%d')}-{random.randint(1000, 9999)}"
    sample = LimsSample(
        sample_code=s_code,
        sample_name=f"{task.project_name}-采样样件01",
        entrust_id=task.id,
        entrust_code=task.task_code,
        sample_type=task.detection_type or "地表水",
        storage_condition="4℃避光冷藏",
        status="待采样"
    )
    db.add(sample)

    # 如果有对应的审批任务节点，同步将委托下单推进至采样流转
    audit_task = db.query(LimsAuditTask).filter(LimsAuditTask.business_name.contains(task.client_name)).first()
    if audit_task:
        audit_task.current_node = "采样前准备"
        history_list = []
        if audit_task.audit_history:
            try:
                history_list = json.loads(audit_task.audit_history)
            except:
                history_list = []
        for h in history_list:
            if h.get("node_name") == "委托下单":
                h["completion_status"] = "已完成"
                h["action"] = "提交采样"
                h["operate_time"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                h["comment"] = "已完成委托下单并下发采样任务，进入第二阶段【采样前准备】"
                break
        history_list.append({
            "node_name": "采样前准备",
            "operator": "超级管理员",
            "action": "待处理",
            "operate_time": "-",
            "completion_status": "处理中",
            "comment": "请指派采样起止时间、调度检测仪器、分配车辆与采样人员、在线绘制点位图并预览分瓶方案"
        })
        audit_task.audit_history = json.dumps(history_list, ensure_ascii=False)

    db.commit()
    return StandardResponse(msg="提交采样成功！该节点已结束，进入第二阶段：采样阶段。")


# ----------------- 核心业务流程 2: 样品管理 -----------------
@router.get("/sample/list", response_model=StandardResponse)
def list_samples(db: Session = Depends(get_db)):
    samples = db.query(LimsSample).order_by(LimsSample.id.desc()).all()
    return StandardResponse(data=[SampleOut.from_orm(s) for s in samples])

@router.post("/sample/create", response_model=StandardResponse)
def create_sample(sample: SampleCreate, db: Session = Depends(get_db)):
    code = f"YP-{datetime.now().strftime('%Y%m%d')}-{random.randint(1000, 9999)}"
    new_sample = LimsSample(
        sample_code=code,
        sample_name=sample.sample_name,
        sample_type=sample.sample_type,
        entrust_code=sample.entrust_code,
        storage_condition=sample.storage_condition,
        status="已登记"
    )
    db.add(new_sample)
    db.commit()
    db.refresh(new_sample)
    return StandardResponse(data=SampleOut.from_orm(new_sample))

# ----------------- 核心业务流程 3: 实验室检测任务 -----------------
@router.get("/detection/list", response_model=StandardResponse)
def list_detections(db: Session = Depends(get_db)):
    items = db.query(LimsDetectionTask).order_by(LimsDetectionTask.id.desc()).all()
    return StandardResponse(data=[DetectionOut.from_orm(d) for d in items])

@router.post("/detection/create", response_model=StandardResponse)
def create_detection(det: DetectionCreate, db: Session = Depends(get_db)):
    code = f"JC-{datetime.now().strftime('%Y%m%d')}-{random.randint(100, 999)}"
    new_det = LimsDetectionTask(
        detection_code=code,
        sample_code=det.sample_code,
        sample_name=det.sample_name,
        item_name=det.item_name,
        method_name=det.method_name,
        unit=det.unit,
        status="待检测",
        inspector="admin"
    )
    db.add(new_det)
    db.commit()
    db.refresh(new_det)
    return StandardResponse(data=DetectionOut.from_orm(new_det))

@router.post("/detection/{det_id}/result", response_model=StandardResponse)
def record_detection_result(det_id: int, res: DetectionUpdateResult, db: Session = Depends(get_db)):
    task = db.query(LimsDetectionTask).filter(LimsDetectionTask.id == det_id).first()
    if not task:
        return StandardResponse(code=404, msg="检测任务未找到")
    task.result_val = res.result_val
    task.status = res.status or "已完成"
    task.complete_time = datetime.now()
    db.commit()
    return StandardResponse(msg="检测结果录入成功")

# ----------------- 核心业务流程 4: 检测报告 -----------------
@router.get("/report/list", response_model=StandardResponse)
def list_reports(db: Session = Depends(get_db)):
    reports = db.query(LimsReport).order_by(LimsReport.id.desc()).all()
    return StandardResponse(data=[ReportOut.from_orm(r) for r in reports])

@router.post("/report/create", response_model=StandardResponse)
def create_report(rep: ReportCreate, db: Session = Depends(get_db)):
    code = f"BG-{datetime.now().strftime('%Y%m%d%H%M')}"
    new_rep = LimsReport(
        report_code=code,
        entrust_code=rep.entrust_code,
        report_title=rep.report_title,
        client_name=rep.client_name,
        issuer="admin",
        status="编制中"
    )
    db.add(new_rep)
    db.commit()
    db.refresh(new_rep)
    return StandardResponse(data=ReportOut.from_orm(new_rep))

@router.post("/report/{rep_id}/issue", response_model=StandardResponse)
def issue_report(rep_id: int, db: Session = Depends(get_db)):
    report = db.query(LimsReport).filter(LimsReport.id == rep_id).first()
    if not report:
        return StandardResponse(code=404, msg="报告不存在")
    report.status = "已签发"
    report.issue_date = datetime.now()
    db.commit()
    return StandardResponse(msg="报告签发成功")

# ----------------- 合同/委托协议接口 -----------------
@router.get("/contract/list", response_model=StandardResponse)
def get_contract_list(
    client_name: str = None,
    contract_name: str = None,
    quotation_code: str = None,
    sign_date: str = None,
    service_type: str = None,
    detection_type: str = None,
    salesman: str = None,
    preset_approver: str = None,
    tested_company: str = None,
    order_cs: str = None,
    db: Session = Depends(get_db)
):
    query = db.query(LimsContract)
    if client_name:
        query = query.filter(LimsContract.client_name.contains(client_name))
    if contract_name:
        query = query.filter(LimsContract.contract_name.contains(contract_name))
    if quotation_code:
        query = query.filter(LimsContract.quotation_code.contains(quotation_code))
    if sign_date:
        query = query.filter(LimsContract.sign_date.contains(sign_date))
    if service_type:
        query = query.filter(LimsContract.service_type == service_type)
    if detection_type:
        query = query.filter(LimsContract.detection_type == detection_type)
    if salesman:
        query = query.filter(LimsContract.salesman.contains(salesman))
    if preset_approver:
        query = query.filter(LimsContract.preset_approver.contains(preset_approver))
    if tested_company:
        query = query.filter(LimsContract.tested_company.contains(tested_company))
    if order_cs:
        query = query.filter(LimsContract.order_cs.contains(order_cs))

    contracts = query.order_by(LimsContract.id.desc()).all()
    res_list = []
    for c in contracts:
        c_dict = ContractOut.from_orm(c).dict()
        if c.monitoring_schemes:
            try:
                c_dict["monitoring_schemes"] = json.loads(c.monitoring_schemes)
            except:
                c_dict["monitoring_schemes"] = []
        else:
            c_dict["monitoring_schemes"] = []
        res_list.append(c_dict)
    return StandardResponse(data=res_list)

@router.post("/contract/create", response_model=StandardResponse)
def create_contract(item: ContractCreate, db: Session = Depends(get_db)):
    contract_data = item.dict()
    # 如果没传入合同编号，自动生成规范合同编号 HT-YYYYMMDDxxx
    if not contract_data.get("contract_code"):
        today_str = datetime.now().strftime("%Y%m%d")
        rand_suffix = random.randint(100, 999)
        contract_data["contract_code"] = f"HT-{today_str}{rand_suffix}"

    # 若合同金额未计算总价不含税，默认同步
    if not contract_data.get("total_amount_excl_tax") and contract_data.get("contract_amount"):
        contract_data["total_amount_excl_tax"] = round(contract_data["contract_amount"] / 1.06, 2)

    # 监测方案转化为 JSON 字符串存储
    if isinstance(contract_data.get("monitoring_schemes"), (list, dict)):
        contract_data["monitoring_schemes"] = json.dumps(contract_data["monitoring_schemes"], ensure_ascii=False)

    contract = LimsContract(**contract_data)
    db.add(contract)
    db.commit()
    db.refresh(contract)

    # 自动在工作流中生成对应的审批任务（报价审核节点）
    init_history = [
        {
            "node_name": "报价登记",
            "operator": "超级管理员",
            "action": "提交申请",
            "operate_time": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "completion_status": "已完成",
            "comment": f"完成报价初稿及监测方案编制（合同金额：¥{contract.contract_amount}），提交进入报价审核流程"
        },
        {
            "node_name": "报价审核",
            "operator": "超级管理员",
            "action": "待处理",
            "operate_time": "-",
            "completion_status": "处理中",
            "comment": "等待审核人员查验合同、监测方案与费用核算"
        }
    ]

    new_audit = LimsAuditTask(
        business_code=contract.quotation_code or f"BJ-{datetime.now().strftime('%Y%m%d%H%M%S')}",
        business_type="报价审批",
        business_name=f"{contract.client_name} - {contract.contract_name}",
        applicant="超级管理员",
        applicant_dept="市场业务部",
        current_node="报价审核",
        current_handler="超级管理员",
        is_finished="否",
        audit_result="待审核",
        contract_id=contract.id,
        attachment_info=json.dumps([
            {"name": f"{contract.contract_name}_技术规范与报价单.pdf", "size": "2.1 MB", "time": datetime.now().strftime("%Y-%m-%d %H:%M"), "uploader": "超级管理员"}
        ], ensure_ascii=False),
        audit_history=json.dumps(init_history, ensure_ascii=False)
    )
    db.add(new_audit)
    db.commit()

    res_out = ContractOut.from_orm(contract).dict()
    if contract.monitoring_schemes:
        try:
            res_out["monitoring_schemes"] = json.loads(contract.monitoring_schemes)
        except:
            res_out["monitoring_schemes"] = []
    return StandardResponse(msg="合同登记创建成功，已提交进入报价审核流转！", data=res_out)

@router.delete("/contract/delete/{contract_id}", response_model=StandardResponse)
def delete_contract(contract_id: int, db: Session = Depends(get_db)):
    contract = db.query(LimsContract).filter(LimsContract.id == contract_id).first()
    if not contract:
        return StandardResponse(code=404, msg="合同不存在")
    db.delete(contract)
    db.commit()
    return StandardResponse(msg="删除成功")

# ----------------- 任务列表 - 审批任务与报价审核接口 -----------------
@router.get("/audit/task/list", response_model=StandardResponse)
def get_audit_task_list(
    business_code: str = None,
    applicant: str = None,
    business_name: str = None,
    current_node: str = None,
    current_handler: str = None,
    is_finished: str = None,
    audit_result: str = None,
    db: Session = Depends(get_db)
):
    query = db.query(LimsAuditTask)
    if business_code:
        query = query.filter(LimsAuditTask.business_code.contains(business_code.strip()))
    if applicant:
        query = query.filter(LimsAuditTask.applicant.contains(applicant.strip()))
    if business_name:
        query = query.filter(LimsAuditTask.business_name.contains(business_name.strip()))
    if current_node:
        query = query.filter(LimsAuditTask.current_node == current_node)
    if current_handler:
        query = query.filter(LimsAuditTask.current_handler.contains(current_handler.strip()))
    if is_finished:
        query = query.filter(LimsAuditTask.is_finished == is_finished)
    if audit_result:
        query = query.filter(LimsAuditTask.audit_result == audit_result)

    tasks = query.order_by(LimsAuditTask.id.desc()).all()
    res = []
    for t in tasks:
        item = AuditTaskOut.from_orm(t).dict()
        if t.attachment_info:
            try:
                item["attachments"] = json.loads(t.attachment_info)
            except:
                item["attachments"] = []
        else:
            item["attachments"] = []
        res.append(item)

    return StandardResponse(data=res)

@router.get("/audit/task/{task_id}/detail", response_model=StandardResponse)
def get_audit_task_detail(task_id: int, db: Session = Depends(get_db)):
    task = db.query(LimsAuditTask).filter(LimsAuditTask.id == task_id).first()
    if not task:
        return StandardResponse(code=404, msg="审批任务不存在")

    task_data = AuditTaskOut.from_orm(task).dict()
    if task.attachment_info:
        try:
            task_data["attachments"] = json.loads(task.attachment_info)
        except:
            task_data["attachments"] = []
    else:
        task_data["attachments"] = []

    # 解析审批历史
    if task.audit_history:
        try:
            task_data["history"] = json.loads(task.audit_history)
        except:
            task_data["history"] = []
    else:
        # 默认生成历史链路
        task_data["history"] = [
            {
                "node_name": "报价登记",
                "operator": "超级管理员",
                "action": "提交申请",
                "operate_time": task.apply_time.strftime("%Y-%m-%d %H:%M:%S") if task.apply_time else "2026-09-01 10:00:00",
                "completion_status": "已完成",
                "comment": "完成报价编制并提交审核"
            },
            {
                "node_name": "报价审核",
                "operator": "超级管理员",
                "action": task.audit_result if task.is_finished == "是" else "待处理",
                "operate_time": task.audit_time.strftime("%Y-%m-%d %H:%M:%S") if task.audit_time else "-",
                "completion_status": "已完成" if task.is_finished == "是" else "处理中",
                "comment": task.audit_comment or ("已完成审批" if task.is_finished == "是" else "等待审核人员查验合同、监测方案与费用核算")
            }
        ]

    # 关联查询完整的合同/报价信息
    contract_data = None
    if task.contract_id:
        contract = db.query(LimsContract).filter(LimsContract.id == task.contract_id).first()
        if contract:
            contract_data = ContractOut.from_orm(contract).dict()
            if contract.monitoring_schemes:
                try:
                    contract_data["monitoring_schemes"] = json.loads(contract.monitoring_schemes)
                except:
                    contract_data["monitoring_schemes"] = []
            else:
                contract_data["monitoring_schemes"] = []

    # 如果没有绑定或者未找到，给出一个规范的兜底数据对象
    if not contract_data:
        contract_data = {
            "client_name": "杭州余杭区生态环保重点监管单位",
            "client_address": "浙江省杭州市余杭区文一西路高新产业园",
            "client_fax": "0571-88991122",
            "client_contact": "周工",
            "client_email": "zhou@yuhang-eco.com",
            "client_phone": "13858009988",
            "client_tel": "0571-88993344",
            "tested_company": "杭州余杭区精细化工产业园",
            "tested_address": "浙江省杭州市余杭区塘栖镇工业区38号",
            "tested_fax": "0571-88995566",
            "tested_contact": "钱经理",
            "tested_email": "qian@hangzhou-chem.com",
            "tested_phone": "13958112233",
            "tested_tel": "0571-88997788",
            "contract_code": "HT-20260901-008",
            "contract_name": task.business_name,
            "business_type": "报价审核",
            "detection_type": "环境综合检测",
            "seal_type": "CMA资质印章",
            "service_type": "委托检测",
            "sign_date": "2026-09-01",
            "start_date": "2026-09-01",
            "end_date": "2027-08-31",
            "sample_delivery_method": "现场采样上门",
            "accept_subcontract": "是",
            "is_judge": "是",
            "use_non_standard_method": "否",
            "salesman": "超级管理员",
            "sample_disposal": "实验室规范处置留样",
            "discount": 95.0,
            "contract_amount": 48500.0,
            "total_amount_excl_tax": 45754.72,
            "quotation_code": task.business_code,
            "preset_approver": "超级管理员",
            "order_cs": "超级管理员",
            "remark": "包含地表水常规九项、厂界无组织废气及土壤重金属检测。",
            "one_order_one_pool": "是",
            "report_type": "中文正式报告(带CMA章)",
            "report_requirement": "出具盖章纸质版2份并提供PDF电子加密件",
            "report_count": 2,
            "report_fetch_method": "顺丰快递到付",
            "report_fetch_company": "杭州余杭区生态环保重点监管单位",
            "report_fetch_address": "浙江省杭州市余杭区文一西路高新产业园1号楼",
            "recipient_name": "周工",
            "recipient_phone": "13858009988",
            "invoice_type": "增值税专用发票(6%)",
            "invoice_title": "杭州余杭区生态环保重点监管单位",
            "taxpayer_id": "91330110MA28ABC123",
            "invoice_address": "浙江省杭州市余杭区文一西路高新产业园",
            "invoice_phone": "0571-88993344",
            "bank_name": "中国工商银行杭州未来科技城支行",
            "bank_account": "1202020209900012345",
            "monitoring_schemes": [
                {
                    "detection_cycle": "1天",
                    "monitoring_type": "地表水",
                    "scheme_name": "园区总排口及周边地表水质季度检测方案",
                    "point_names": "雨水排口1#、污水总排口2#、厂界外受纳水体3#",
                    "point_count": 3,
                    "cycle_days": 1,
                    "frequency": "1次/季",
                    "subtotal": 18500,
                    "items": [
                        {"item_name": "pH值", "price": 40, "cycle": "1天", "method_name": "GB/T 6920-1986", "point_names": "1#, 2#, 3#"},
                        {"item_name": "化学需氧量(CODcr)", "price": 120, "cycle": "1天", "method_name": "HJ 828-2017", "point_names": "1#, 2#, 3#"},
                        {"item_name": "氨氮(NH3-N)", "price": 100, "cycle": "1天", "method_name": "HJ 535-2009", "point_names": "1#, 2#, 3#"},
                        {"item_name": "总磷(TP)", "price": 110, "cycle": "1天", "method_name": "GB 11893-1989", "point_names": "1#, 2#, 3#"},
                        {"item_name": "六价铬", "price": 150, "cycle": "1天", "method_name": "GB 7467-1987", "point_names": "1#, 2#, 3#"}
                    ]
                },
                {
                    "detection_cycle": "1天",
                    "monitoring_type": "废气有组织",
                    "scheme_name": "车间排气筒VOCs及颗粒物检测方案",
                    "point_names": "烘干车间排气筒1#、涂装车间排气筒2#",
                    "point_count": 2,
                    "cycle_days": 1,
                    "frequency": "1次/季",
                    "subtotal": 30000,
                    "items": [
                        {"item_name": "非甲烷总烃", "price": 280, "cycle": "1天", "method_name": "HJ 38-2017", "point_names": "1#, 2#"},
                        {"item_name": "颗粒物", "price": 200, "cycle": "1天", "method_name": "GB/T 16157-1996", "point_names": "1#, 2#"}
                    ]
                }
            ]
        }

    return StandardResponse(data={
        "task": task_data,
        "contract": contract_data
    })

@router.post("/audit/task/{task_id}/approve", response_model=StandardResponse)
def approve_audit_task(task_id: int, payload: AuditTaskApprove, db: Session = Depends(get_db)):
    task = db.query(LimsAuditTask).filter(LimsAuditTask.id == task_id).first()
    if not task:
        return StandardResponse(code=404, msg="审批任务不存在")

    action_name = payload.action
    task.audit_result = action_name
    task.audit_comment = payload.audit_comment
    task.audit_time = datetime.now()
    now_str = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    # 1. 如果提交了修改后的合同细节及监测方案（合同录入节点支持再度修改保存）
    if payload.contract_data and task.contract_id:
        contract = db.query(LimsContract).filter(LimsContract.id == task.contract_id).first()
        if contract and isinstance(payload.contract_data, dict):
            c_data = payload.contract_data
            for key, val in c_data.items():
                if hasattr(contract, key) and key not in ["id", "create_time"]:
                    if key == "monitoring_schemes" and isinstance(val, (list, dict)):
                        setattr(contract, key, json.dumps(val, ensure_ascii=False))
                    else:
                        setattr(contract, key, val)
            # 如果金额更新，同步业务名称等
            if c_data.get("contract_name"):
                task.business_name = f"{contract.client_name} - {contract.contract_name}"
            db.commit()

    # 2. 更新审批历史记录
    history_list = []
    if task.audit_history:
        try:
            history_list = json.loads(task.audit_history)
        except:
            history_list = []

    if not history_list:
        history_list = [
            {
                "node_name": "报价登记",
                "operator": "超级管理员",
                "action": "提交申请",
                "operate_time": task.apply_time.strftime("%Y-%m-%d %H:%M:%S") if task.apply_time else now_str,
                "completion_status": "已完成",
                "comment": "完成报价编制并提交审核"
            }
        ]

    # 判断当前所在节点
    curr_node = task.current_node

    if curr_node == "报价审核":
        if action_name in ["同意", "通过"]:
            # 报价审核同意后 -> 流转进入【合同录入】节点！
            task.current_node = "合同录入"
            task.current_handler = "超级管理员"
            task.is_finished = "否"
            task.audit_result = "待录入"
            completion = "已完成"

            # 标记当前“报价审核”节点已完成
            updated = False
            for h in history_list:
                if h.get("node_name") == "报价审核":
                    h["operator"] = "超级管理员"
                    h["action"] = action_name
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or "报价审核通过，进入合同录入与细节完善阶段"
                    updated = True
                    break
            if not updated:
                history_list.append({
                    "node_name": "报价审核",
                    "operator": "超级管理员",
                    "action": action_name,
                    "operate_time": now_str,
                    "completion_status": completion,
                    "comment": payload.audit_comment or "报价审核通过，进入合同录入与细节完善阶段"
                })

            # 追加【合同录入】节点待办
            history_list.append({
                "node_name": "合同录入",
                "operator": "超级管理员",
                "action": "待处理",
                "operate_time": "-",
                "completion_status": "处理中",
                "comment": "等待完善合同条款、受检信息、监测方案及计费细节"
            })

        elif action_name == "驳回":
            task.current_node = "流程终止 / 已驳回"
            task.is_finished = "是"
            task.audit_result = "驳回"
            completion = "已驳回"
            if task.contract_id:
                contract = db.query(LimsContract).filter(LimsContract.id == task.contract_id).first()
                if contract:
                    contract.status = "已驳回"
            for h in history_list:
                if h.get("node_name") == "报价审核":
                    h["operator"] = "超级管理员"
                    h["action"] = action_name
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or "审核驳回，流程终止"
                    break

        elif action_name in ["回退", "退回"]:
            task.current_node = "报价登记（回退修改）"
            task.is_finished = "否"
            task.audit_result = "已回退"
            completion = "已回退"
            for h in history_list:
                if h.get("node_name") == "报价审核":
                    h["operator"] = "超级管理员"
                    h["action"] = action_name
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or "回退修改"
                    break
            history_list.append({
                "node_name": "报价登记（回退修改）",
                "operator": "超级管理员",
                "action": "待处理",
                "operate_time": "-",
                "completion_status": "处理中",
                "comment": f"等待重新修改报价并再次提审（回退原因：{payload.audit_comment}）"
            })

    elif curr_node == "合同录入":
        if action_name in ["同意", "通过", "提交"]:
            # 合同录入完成后，点同意 -> 流转至下一节点【合同确认】
            task.current_node = "合同确认"
            task.current_handler = "超级管理员"
            task.is_finished = "否"
            task.audit_result = "待确认"
            completion = "已完成"

            # 标记“合同录入”节点完成
            updated = False
            for h in history_list:
                if h.get("node_name") == "合同录入":
                    h["operator"] = "超级管理员"
                    h["action"] = "同意（完成录入）"
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or "已核对并完善合同信息与监测方案细节，确认无误，提交合同确认"
                    updated = True
                    break
            if not updated:
                history_list.append({
                    "node_name": "合同录入",
                    "operator": "超级管理员",
                    "action": "同意（完成录入）",
                    "operate_time": now_str,
                    "completion_status": completion,
                    "comment": payload.audit_comment or "已核对并完善合同信息与监测方案细节，确认无误，提交合同确认"
                })

            # 追加【合同确认】节点
            history_list.append({
                "node_name": "合同确认",
                "operator": "超级管理员",
                "action": "待处理",
                "operate_time": "-",
                "completion_status": "处理中",
                "comment": "等待对完善后的合同条款与方案进行最终确认"
            })

        elif action_name == "驳回":
            task.current_node = "流程终止 / 已驳回"
            task.is_finished = "是"
            task.audit_result = "驳回"
            completion = "已驳回"
            for h in history_list:
                if h.get("node_name") == "合同录入":
                    h["operator"] = "超级管理员"
                    h["action"] = action_name
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or "录入不合格，驳回"
                    break

        elif action_name in ["回退", "退回"]:
            task.current_node = "报价审核"
            task.is_finished = "否"
            task.audit_result = "已回退"
            completion = "已回退"
            for h in history_list:
                if h.get("node_name") == "合同录入":
                    h["operator"] = "超级管理员"
                    h["action"] = action_name
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or "回退至报价审核节点"
                    break
            history_list.append({
                "node_name": "报价审核",
                "operator": "超级管理员",
                "action": "待处理",
                "operate_time": "-",
                "completion_status": "处理中",
                "comment": f"退回报价审核重新复查（原因：{payload.audit_comment}）"
            })

    elif curr_node == "合同确认":
        if action_name in ["同意", "通过"]:
            # 合同确认节点与报价审核并无二致，点击同意之后，流转进入【委托下单】节点
            task.current_node = "委托下单"
            task.current_handler = "超级管理员"
            task.is_finished = "否"
            task.audit_result = "待下单"
            completion = "已完成"
            if task.contract_id:
                contract = db.query(LimsContract).filter(LimsContract.id == task.contract_id).first()
                if contract:
                    contract.status = "已生效"

            # 标记“合同确认”节点完成
            updated = False
            for h in history_list:
                if h.get("node_name") == "合同确认":
                    h["operator"] = "超级管理员"
                    h["action"] = action_name
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or "合同各项条款及方案确认无误，同意流转进入委托下单"
                    updated = True
                    break
            if not updated:
                history_list.append({
                    "node_name": "合同确认",
                    "operator": "超级管理员",
                    "action": action_name,
                    "operate_time": now_str,
                    "completion_status": completion,
                    "comment": payload.audit_comment or "合同各项条款及方案确认无误，同意流转进入委托下单"
                })

            # 追加【委托下单】节点待办
            history_list.append({
                "node_name": "委托下单",
                "operator": "超级管理员",
                "action": "待处理",
                "operate_time": "-",
                "completion_status": "处理中",
                "comment": "合同确认已生效，等待根据监测方案正式下发委托单与采样任务"
            })

        elif action_name == "驳回":
            task.current_node = "流程终止 / 已驳回"
            task.is_finished = "是"
            task.audit_result = "驳回"
            completion = "已驳回"
            for h in history_list:
                if h.get("node_name") == "合同确认":
                    h["operator"] = "超级管理员"
                    h["action"] = action_name
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or "合同确认不通过，驳回终止"
                    break

        elif action_name in ["回退", "退回"]:
            task.current_node = "合同录入"
            task.is_finished = "否"
            task.audit_result = "已回退"
            completion = "已回退"
            for h in history_list:
                if h.get("node_name") == "合同确认":
                    h["operator"] = "超级管理员"
                    h["action"] = action_name
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or "回退至合同录入节点修改细节"
                    break
            history_list.append({
                "node_name": "合同录入",
                "operator": "超级管理员",
                "action": "待处理",
                "operate_time": "-",
                "completion_status": "处理中",
                "comment": f"退回合同录入重新修改完善（原因：{payload.audit_comment}）"
            })

    elif curr_node in ["采样前准备", "采样任务实施"]:
        if action_name == "同意":
            # 校验必填项：采样时间、仪器、人员（采样员与采样队长）
            prep_data = payload.sampling_prep_data or {}
            start_time = prep_data.get("start_time")
            end_time = prep_data.get("end_time")
            instruments = prep_data.get("instruments") or []
            personnel = prep_data.get("personnel") or []

            if not start_time or not end_time:
                return StandardResponse(code=400, msg="【采样开始时间】与【采样结束时间】为必填项，请在第二栏完整选择！")
            if len(instruments) == 0:
                return StandardResponse(code=400, msg="【检测仪器】为必填项，请在第三栏添加至少 1 台检测仪器！")
            if len(personnel) == 0:
                return StandardResponse(code=400, msg="【采样人员】为必填项，请在第四栏添加至少 1 名采样人员！")
            has_leader = any(p.get("is_leader") or p.get("role") == "采样队长" for p in personnel)
            if not has_leader:
                return StandardResponse(code=400, msg="【采样人员】中必须指定至少 1 名【采样队长】，请核对人员身份设置！")

            # 保存持久化采样前准备数据
            prep_record = db.query(LimsSamplingPreparation).filter(LimsSamplingPreparation.audit_task_id == task_id).first()
            if not prep_record:
                prep_record = LimsSamplingPreparation(
                    audit_task_id=task_id,
                    contract_id=task.contract_id,
                    start_time=start_time,
                    end_time=end_time,
                    remark=prep_data.get("remark", ""),
                    rule_name=prep_data.get("rule_name", "《地表水和污水监测技术规范 HJ 91.1-2019》"),
                    instruments_json=json.dumps(instruments, ensure_ascii=False),
                    vehicles_json=json.dumps(prep_data.get("vehicles", []), ensure_ascii=False),
                    personnel_json=json.dumps(personnel, ensure_ascii=False),
                    canvas_data_json=json.dumps(prep_data.get("canvas_data", {}), ensure_ascii=False) if prep_data.get("canvas_data") else None,
                    bottles_preview_json=json.dumps(prep_data.get("bottles_preview", []), ensure_ascii=False),
                    status="已完成"
                )
                db.add(prep_record)
            else:
                prep_record.start_time = start_time
                prep_record.end_time = end_time
                prep_record.remark = prep_data.get("remark", "")
                prep_record.rule_name = prep_data.get("rule_name", "《地表水和污水监测技术规范 HJ 91.1-2019》")
                prep_record.instruments_json = json.dumps(instruments, ensure_ascii=False)
                prep_record.vehicles_json = json.dumps(prep_data.get("vehicles", []), ensure_ascii=False)
                prep_record.personnel_json = json.dumps(personnel, ensure_ascii=False)
                if prep_data.get("canvas_data"):
                    prep_record.canvas_data_json = json.dumps(prep_data.get("canvas_data", {}), ensure_ascii=False)
                prep_record.bottles_preview_json = json.dumps(prep_data.get("bottles_preview", []), ensure_ascii=False)
                prep_record.status = "已完成"

            task.current_node = "现场确认方案"
            task.is_finished = "否"
            task.audit_result = "待现场确认"
            completion = "已完成"

            for h in history_list:
                if h.get("node_name") in ["采样前准备", "采样任务实施"]:
                    h["operator"] = payload.handler or "超级管理员"
                    h["action"] = action_name
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or f"采样准备完成，已指派{len(personnel)}人、{len(instruments)}台仪器，预约时间段：{start_time} ~ {end_time}"
                    break
            history_list.append({
                "node_name": "现场确认方案",
                "operator": "现场采样组",
                "action": "待处理",
                "operate_time": "-",
                "completion_status": "处理中",
                "comment": "等待采样工作组抵达现场，与企业环境负责人现场确认点位排布、工况条件与安全防范方案"
            })

        elif action_name == "驳回":
            task.current_node = "流程终止 / 已驳回"
            task.is_finished = "是"
            task.audit_result = "驳回"
            completion = "已驳回"
            for h in history_list:
                if h.get("node_name") in ["采样前准备", "采样任务实施"]:
                    h["operator"] = payload.handler or "超级管理员"
                    h["action"] = action_name
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or "采样准备审核不通过，流程终止"
                    break

        elif action_name in ["回退", "退回"]:
            task.current_node = "委托下单"
            task.is_finished = "否"
            task.audit_result = "已回退"
            completion = "已回退"
            for h in history_list:
                if h.get("node_name") in ["采样前准备", "采样任务实施"]:
                    h["operator"] = payload.handler or "超级管理员"
                    h["action"] = action_name
                    h["operate_time"] = now_str
                    h["completion_status"] = completion
                    h["comment"] = payload.audit_comment or "回退至委托下单节点"
                    break
            history_list.append({
                "node_name": "委托下单",
                "operator": "超级管理员",
                "action": "待处理",
                "operate_time": "-",
                "completion_status": "处理中",
                "comment": f"采样任务退回委托下单重新调整（原因：{payload.audit_comment}）"
            })

    else:
        # 其他节点通用处理
        task.current_node = "流程结束"
        task.is_finished = "是"
        task.audit_result = action_name

    task.audit_history = json.dumps(history_list, ensure_ascii=False)
    db.commit()
    return StandardResponse(msg=f"审批操作成功：【{action_name}】，任务已流转！")




# ----------------- 采样阶段核心接口 -----------------
@router.get("/sampling/presets", response_model=StandardResponse)
def get_sampling_presets(
    instrument_code: str = None,
    instrument_name: str = None
):
    instruments = [
        {"instrument_code": "YQ-2026-001", "instrument_name": "便携式多参数水质测定仪", "instrument_model": "YSI ProDSS", "reservation_record": "空闲可用", "instrument_type": "现场便携水质仪", "status": "正常在库"},
        {"instrument_code": "YQ-2026-002", "instrument_name": "自动烟尘烟气综合测试仪", "instrument_model": "崂应3012H", "reservation_record": "空闲可用", "instrument_type": "废气采样仪器", "status": "正常在库"},
        {"instrument_code": "YQ-2026-003", "instrument_name": "便携式紫外差分烟气分析仪", "instrument_model": "Gasmet DX4000", "reservation_record": "空闲可用", "instrument_type": "现场烟气分析仪", "status": "正常在库"},
        {"instrument_code": "YQ-2026-004", "instrument_name": "多功能精密声级计", "instrument_model": "AWA6228+", "reservation_record": "空闲可用", "instrument_type": "噪声检测仪", "status": "正常在库"},
        {"instrument_code": "YQ-2026-005", "instrument_name": "便携式氢火焰离子化(FID)分析仪", "instrument_model": "Thermo FID-700", "reservation_record": "09-29已被预约", "instrument_type": "VOCs现场分析仪", "status": "正常在库"},
        {"instrument_code": "YQ-2026-006", "instrument_name": "智能大流量TSP/PM10颗粒物采样器", "instrument_model": "崂应2030", "reservation_record": "空闲可用", "instrument_type": "空气颗粒物采样器", "status": "正常在库"},
        {"instrument_code": "YQ-2026-007", "instrument_name": "便携式重金属快速水质测定仪", "instrument_model": "PDV6000plus", "reservation_record": "空闲可用", "instrument_type": "重金属快速测定仪", "status": "正常在库"},
        {"instrument_code": "YQ-2026-008", "instrument_name": "便携式明渠流速流量仪", "instrument_model": "LS300-A", "reservation_record": "空闲可用", "instrument_type": "水文流速仪", "status": "正常在库"}
    ]

    if instrument_code:
        instruments = [i for i in instruments if instrument_code.strip().lower() in i["instrument_code"].lower()]
    if instrument_name:
        instruments = [i for i in instruments if instrument_name.strip() in i["instrument_name"]]

    vehicles = [
        {"plate_number": "浙A·8889L (环境采样专车01)", "reservation_record": "空闲可用", "status": "车况良好"},
        {"plate_number": "浙A·5678B (外勤应急检测车02)", "reservation_record": "空闲可用", "status": "车况良好"},
        {"plate_number": "浙A·3342E (废气采样工作车03)", "reservation_record": "空闲可用", "status": "车况良好"},
        {"plate_number": "浙A·9912A (普通外勤巡检车04)", "reservation_record": "明日维保排班", "status": "维保中"}
    ]

    personnel = [
        {"name": "张建国", "reservation_record": "在岗空闲", "role": "采样队长", "is_leader": True, "qualification": "水质/废气现场高级采样证"},
        {"name": "李明辉", "reservation_record": "在岗空闲", "role": "采样员", "is_leader": False, "qualification": "地表水现场采样资质"},
        {"name": "王子轩", "reservation_record": "在岗空闲", "role": "采样员", "is_leader": False, "qualification": "噪声与振动现场检测资质"},
        {"name": "赵海燕", "reservation_record": "在岗空闲", "role": "采样员", "is_leader": False, "qualification": "固定污染源废气监测资质"},
        {"name": "陈小敏", "reservation_record": "在岗空闲", "role": "采样队长", "is_leader": False, "qualification": "生态环境现场采样综合资质"},
        {"name": "周建平", "reservation_record": "外勤出差中", "role": "采样员", "is_leader": False, "qualification": "土壤与沉积物采样资质"}
    ]

    rules = [
        {"rule_name": "《地表水和污水监测技术规范 HJ 91.1-2019》", "is_default": True, "category": "水质类"},
        {"rule_name": "《固定污染源废气监测技术规范 HJ/T 397-2007》", "is_default": False, "category": "气类"},
        {"rule_name": "《环境空气质量手工监测技术规范 HJ 194-2017》", "is_default": False, "category": "气类"},
        {"rule_name": "《土壤环境监测技术规范 HJ/T 166-2004》", "is_default": False, "category": "土壤类"},
        {"rule_name": "《声环境质量标准与测量规范 GB 3096-2008》", "is_default": False, "category": "噪声类"},
        {"rule_name": "《地下水环境监测技术规范 HJ 164-2020》", "is_default": False, "category": "水质类"}
    ]

    return StandardResponse(data={
        "instruments": instruments,
        "vehicles": vehicles,
        "personnel": personnel,
        "rules": rules
    })

@router.get("/sampling/preparation/{task_id}", response_model=StandardResponse)
def get_sampling_preparation(task_id: int, db: Session = Depends(get_db)):
    task = db.query(LimsAuditTask).filter(LimsAuditTask.id == task_id).first()
    if not task:
        return StandardResponse(code=404, msg="任务不存在")

    record = db.query(LimsSamplingPreparation).filter(LimsSamplingPreparation.audit_task_id == task_id).first()
    if record:
        res = {
            "id": record.id,
            "audit_task_id": record.audit_task_id,
            "contract_id": record.contract_id,
            "start_time": record.start_time or "",
            "end_time": record.end_time or "",
            "remark": record.remark or "",
            "rule_name": record.rule_name or "《地表水和污水监测技术规范 HJ 91.1-2019》",
            "instruments": json.loads(record.instruments_json) if record.instruments_json else [],
            "vehicles": json.loads(record.vehicles_json) if record.vehicles_json else [],
            "personnel": json.loads(record.personnel_json) if record.personnel_json else [],
            "canvas_data": json.loads(record.canvas_data_json) if record.canvas_data_json else None,
            "bottles_preview": json.loads(record.bottles_preview_json) if record.bottles_preview_json else [],
            "status": record.status
        }
        return StandardResponse(data=res)

    # 默认初始化
    contract = None
    if task.contract_id:
        contract = db.query(LimsContract).filter(LimsContract.id == task.contract_id).first()

    # 提取点位
    point_names_list = []
    if contract and contract.monitoring_schemes:
        try:
            schemes = json.loads(contract.monitoring_schemes)
            for sc in schemes:
                p_str = sc.get("point_names", "")
                for p in p_str.replace("、", ",").replace("，", ",").split(","):
                    p = p.strip()
                    if p and p not in point_names_list:
                        point_names_list.append(p)
        except:
            pass

    if not point_names_list:
        point_names_list = ["1#综合废水总排口", "2#雨水排放口", "厂界无组织监控点"]

    # 默认分瓶预览
    default_bottles = [
        {"bottle_code": "YP2026-B01", "container": "1000ml 聚乙烯塑料瓶", "preservative": "加硫酸调至 pH < 2，4℃冷藏", "items": "CODcr、氨氮、总磷、总氮", "volume": "1000 mL", "points": "、".join(point_names_list[:2])},
        {"bottle_code": "YP2026-B02", "container": "500ml 棕色磨口玻璃瓶", "preservative": "加NaOH固定(pH>12)并加CuSO4，4℃避光", "items": "挥发酚、氰化物", "volume": "500 mL", "points": point_names_list[0]},
        {"bottle_code": "YP2026-B03", "container": "1000ml 广口棕色玻璃瓶", "preservative": "加盐酸酸化至 pH < 2，4℃冷藏", "items": "石油类、动植物油", "volume": "1000 mL", "points": point_names_list[0]},
        {"bottle_code": "YP2026-B04", "container": "500ml 聚乙烯塑料瓶", "preservative": "加高纯浓硝酸酸化至 pH < 2", "items": "铜、锌、铅、镉、总铬", "volume": "500 mL", "points": "、".join(point_names_list[:2])},
        {"bottle_code": "YP2026-B05", "container": "10L 氟塑料专用采气袋", "preservative": "避光常温密闭保存，24h内完成分析", "items": "非甲烷总烃、苯系物", "volume": "10 L", "points": "厂界上风向、厂界下风向"}
    ]

    return StandardResponse(data={
        "audit_task_id": task_id,
        "contract_id": task.contract_id,
        "start_time": "",
        "end_time": "",
        "remark": "",
        "rule_name": "《地表水和污水监测技术规范 HJ 91.1-2019》",
        "instruments": [
            {"instrument_code": "YQ-2026-001", "instrument_name": "便携式多参数水质测定仪", "instrument_model": "YSI ProDSS", "reservation_record": "空闲可用", "instrument_type": "现场便携水质仪", "status": "正常在库"}
        ],
        "vehicles": [
            {"plate_number": "浙A·8889L (环境采样专车01)", "reservation_record": "空闲可用", "status": "车况良好"}
        ],
        "personnel": [
            {"name": "张建国", "reservation_record": "在岗空闲", "role": "采样队长", "is_leader": True, "qualification": "水质/废气现场高级采样证"},
            {"name": "李明辉", "reservation_record": "在岗空闲", "role": "采样员", "is_leader": False, "qualification": "地表水现场采样资质"}
        ],
        "canvas_data": {
            "bg_image": "",
            "markers": [],
            "strokes": []
        },
        "points_list": point_names_list,
        "bottles_preview": default_bottles,
        "status": "草稿"
    })

@router.post("/sampling/preparation/{task_id}/save", response_model=StandardResponse)
def save_sampling_preparation(task_id: int, form_data: SamplingPreparationSave, db: Session = Depends(get_db)):
    task = db.query(LimsAuditTask).filter(LimsAuditTask.id == task_id).first()
    if not task:
        return StandardResponse(code=404, msg="任务不存在")

    record = db.query(LimsSamplingPreparation).filter(LimsSamplingPreparation.audit_task_id == task_id).first()
    if not record:
        record = LimsSamplingPreparation(
            audit_task_id=task_id,
            contract_id=task.contract_id,
            start_time=form_data.start_time,
            end_time=form_data.end_time,
            remark=form_data.remark,
            rule_name=form_data.rule_name,
            instruments_json=json.dumps(form_data.instruments or [], ensure_ascii=False),
            vehicles_json=json.dumps(form_data.vehicles or [], ensure_ascii=False),
            personnel_json=json.dumps(form_data.personnel or [], ensure_ascii=False),
            canvas_data_json=json.dumps(form_data.canvas_data or {}, ensure_ascii=False) if form_data.canvas_data else None,
            bottles_preview_json=json.dumps(form_data.bottles_preview or [], ensure_ascii=False),
            status="已暂存"
        )
        db.add(record)
    else:
        record.start_time = form_data.start_time
        record.end_time = form_data.end_time
        record.remark = form_data.remark
        record.rule_name = form_data.rule_name
        record.instruments_json = json.dumps(form_data.instruments or [], ensure_ascii=False)
        record.vehicles_json = json.dumps(form_data.vehicles or [], ensure_ascii=False)
        record.personnel_json = json.dumps(form_data.personnel or [], ensure_ascii=False)
        if form_data.canvas_data:
            record.canvas_data_json = json.dumps(form_data.canvas_data, ensure_ascii=False)
        record.bottles_preview_json = json.dumps(form_data.bottles_preview or [], ensure_ascii=False)
        record.status = "已暂存"

    db.commit()
    return StandardResponse(msg="采样前准备配置已成功暂存！")





# ----------------- 统计概览数据 -----------------
@router.get("/dashboard/stats", response_model=StandardResponse)
def get_dashboard_stats(db: Session = Depends(get_db)):
    entrust_count = db.query(LimsEntrustTask).count()
    sample_count = db.query(LimsSample).count()
    detection_count = db.query(LimsDetectionTask).count()
    report_count = db.query(LimsReport).count()

    return StandardResponse(data={
        "entrust_count": entrust_count,
        "sample_count": sample_count,
        "detection_count": detection_count,
        "report_count": report_count,
        "pending_audit": 3,
        "active_devices": 18,
        "sample_progress": [
            {"name": "待处理", "value": 12},
            {"name": "检测中", "value": 28},
            {"name": "已完成", "value": 45}
        ]
    })
