from app.core.database import SessionLocal, engine, Base
from app.models.lims import SysUser, LimsEntrustTask, LimsSample, LimsDetectionTask, LimsReport, LimsContract, LimsAuditTask
import json

def init_db():
    Base.metadata.create_all(bind=engine)
    db = SessionLocal()

    # 初始化管理员账号 admin / 123456
    admin = db.query(SysUser).filter(SysUser.username == "admin").first()
    if not admin:
        admin_user = SysUser(
            username="admin",
            password="123456",
            real_name="超级管理员",
            dept_name="国家级环境检测中心",
            status=1,
            mobile="13800138000",
            email="admin@lims.local"
        )
        db.add(admin_user)

    # 预置高质量初始 LIMS 业务流演示数据
    if db.query(LimsEntrustTask).count() == 0:
        t1 = LimsEntrustTask(
            task_code="WT-20260901001",
            client_name="华东环境监测总站",
            contact_person="张工程师",
            contact_phone="13911223344",
            project_name="重点流域地表水质季度例行监测",
            detection_type="地表水检测",
            contract_code="HT-20260810-001",
            contract_name="华东重点流域地表水质年度技术服务合同",
            status="检测中",
            report_deadline="2026-09-30",
            payment_company="华东环境监测总站",
            amount=48500.0,
            paid_amount=48500.0,
            has_quotation="有报价单",
            service_type="委托检测",
            salesman="超级管理员",
            is_subcontract="否",
            monitoring_schemes=json.dumps([
                {
                    "scheme_name": "地表水9项常规水质检测方案",
                    "monitoring_type": "地表水",
                    "point_names": "断面1#、断面2#",
                    "point_count": 2,
                    "cycle_days": 1,
                    "frequency": "1次/季",
                    "subtotal": 12800,
                    "items": [{"item_name": "pH值", "price": 40}, {"item_name": "CODcr", "price": 120}]
                }
            ], ensure_ascii=False),
            remark="按照环保HJ/T标准严格采样与保存",
            creator="超级管理员"
        )
        t2 = LimsEntrustTask(
            task_code="WT-20260910002",
            client_name="绿源化工科技有限公司",
            contact_person="李经理",
            contact_phone="13788990011",
            project_name="排污口工业废水与挥发酚专项合规检测",
            detection_type="工业废水检测",
            contract_code="HT-20260815-002",
            contract_name="绿源化工综合排污口废水与毒性检测协议",
            status="待采样",
            report_deadline="2026-10-15",
            payment_company="绿源化工科技有限公司",
            amount=26000.0,
            paid_amount=15000.0,
            has_quotation="有报价单",
            service_type="委托检测",
            salesman="超级管理员",
            is_subcontract="否",
            monitoring_schemes=json.dumps([
                {
                    "scheme_name": "总排口工业废水毒性检测方案",
                    "monitoring_type": "工业废水",
                    "point_names": "总排口1#",
                    "point_count": 1,
                    "cycle_days": 1,
                    "frequency": "1次/月",
                    "subtotal": 15000,
                    "items": [{"item_name": "CODcr", "price": 120}, {"item_name": "挥发酚", "price": 180}]
                }
            ], ensure_ascii=False),
            remark="重点关注COD与总磷超标情况",
            creator="超级管理员"
        )
        t3 = LimsEntrustTask(
            task_code="WT-20260918003",
            client_name="市政自来水供水集团",
            contact_person="王主任",
            contact_phone="13566778899",
            project_name="生活饮用水管网末梢水水质安全普查",
            detection_type="生活饮用水",
            contract_code="HT-20260825-003",
            contract_name="市政饮用水末梢水质抽查协议",
            status="待下单",
            report_deadline="2026-10-20",
            payment_company="市政自来水供水集团",
            amount=32000.0,
            paid_amount=0.0,
            has_quotation="有报价单",
            service_type="委托检测",
            salesman="超级管理员",
            is_subcontract="否",
            monitoring_schemes=json.dumps([
                {
                    "scheme_name": "饮用水末梢常规理化与微生物方案",
                    "monitoring_type": "生活饮用水",
                    "point_names": "小区泵房1#、居民龙头2#",
                    "point_count": 2,
                    "cycle_days": 1,
                    "frequency": "1次",
                    "subtotal": 22000,
                    "items": [{"item_name": "菌落总数", "price": 80}, {"item_name": "总大肠菌群", "price": 90}]
                }
            ], ensure_ascii=False),
            remark="普查菌落总数、色度、浊度与重金属",
            creator="超级管理员"
        )
        db.add_all([t1, t2, t3])

        s1 = LimsSample(
            sample_code="YP-20260901-01",
            sample_name="1号取样井地表水样",
            entrust_code="WT-20260901001",
            sample_type="地表水",
            storage_condition="4℃避光冷藏",
            status="检测中"
        )
        s2 = LimsSample(
            sample_code="YP-20260901-02",
            sample_name="总排污口废水样",
            entrust_code="WT-20260910002",
            sample_type="工业废水",
            storage_condition="常温密闭",
            status="已入库"
        )
        s3 = LimsSample(
            sample_code="YP-20260901-03",
            sample_name="管网末梢自来水样A",
            entrust_code="WT-20260918003",
            sample_type="生活饮用水",
            storage_condition="低温冷藏",
            status="已登记"
        )
        db.add_all([s1, s2, s3])

        d1 = LimsDetectionTask(
            detection_code="JC-20260901-001",
            sample_code="YP-20260901-01",
            sample_name="1号取样井地表水样",
            item_name="化学需氧量 (CODcr)",
            method_name="HJ 828-2017 重铬酸盐法",
            result_val="18.5",
            unit="mg/L",
            status="已审核",
            inspector="admin",
            reviewer="技术主任"
        )
        d2 = LimsDetectionTask(
            detection_code="JC-20260901-002",
            sample_code="YP-20260901-01",
            sample_name="1号取样井地表水样",
            item_name="氨氮 (NH3-N)",
            method_name="HJ 535-2009 纳氏试剂分光光度法",
            result_val="0.42",
            unit="mg/L",
            status="已完成",
            inspector="admin",
            reviewer="技术主任"
        )
        d3 = LimsDetectionTask(
            detection_code="JC-20260901-003",
            sample_code="YP-20260901-02",
            sample_name="总排污口废水样",
            item_name="总磷 (TP)",
            method_name="GB/T 11893-1989 钼酸铵分光光度法",
            result_val=None,
            unit="mg/L",
            status="待检测",
            inspector="admin"
        )
        db.add_all([d1, d2, d3])

        r1 = LimsReport(
            report_code="BG-20260915001",
            entrust_code="WT-20260901001",
            report_title="华东环境监测总站地表水专项分析报告",
            client_name="华东环境监测总站",
            issuer="admin",
            status="已签发"
        )
        db.add(r1)
        db.commit()

    # 检查并初始化合同历史示例数据
    if db.query(LimsContract).count() == 0:
        c1 = LimsContract(
            client_name="江苏恒瑞医药科技股份有限公司",
            client_address="江苏省连云港市经济技术开发区黄河路18号",
            client_fax="0518-81234567",
            client_contact="李工",
            client_email="ligong@hengrui.com",
            client_phone="13951234567",
            client_tel="0518-88889999",
            tested_company="江苏恒瑞医药科技股份有限公司(连云港厂区)",
            tested_address="江苏省连云港市经济技术开发区黄河路18号",
            tested_fax="0518-81234567",
            tested_contact="王主管",
            tested_email="wangzg@hengrui.com",
            tested_phone="13851238888",
            tested_tel="0518-88886666",
            contract_code="HT-20260901001",
            contract_name="2026年度水质与废气排污定期委托检测合同",
            biz_type="环境污染(HW)",
            detection_type="环境检测",
            seal_type="CMA资质印章",
            service_type="委托检测",
            sign_date="2026-09-01",
            start_date="2026-09-01",
            end_date="2027-08-31",
            sample_delivery_method="自送样",
            accept_subcontract="是",
            is_judge="是",
            use_non_standard_method="否",
            salesman="张经理",
            sample_disposal="实验室统一留样销毁",
            discount=95.0,
            contract_amount=51410.0,
            total_amount_excl_tax=48500.0,
            quotation_code="BJ-20260901001",
            preset_approver="李技术总工",
            order_cs="王敏",
            remark="常规排污检测，每月10日完成第一轮采样并反馈分析结果",
            one_order_one_pool="是",
            report_type="中文正式报告(带CMA章)",
            report_requirement="常规出具",
            report_count=3,
            report_fetch_method="顺丰快递到付",
            report_fetch_company="江苏恒瑞医药科技股份有限公司",
            report_fetch_address="连云港市经济技术开发区黄河路18号综合楼302室",
            recipient_name="李工",
            recipient_phone="13951234567",
            invoice_type="增值税专用发票(6%)",
            invoice_title="江苏恒瑞医药科技股份有限公司",
            taxpayer_id="91320700138992015K",
            invoice_address="江苏省连云港市经济技术开发区黄河路18号",
            invoice_phone="0518-88889999",
            bank_name="中国建设银行连云港分行营业部",
            bank_account="32001673636052501234",
            monitoring_schemes='''[
                {
                    "detection_cycle": "定期监测",
                    "monitoring_type": "废水排污监测",
                    "scheme_name": "厂区废水排放口月度常规全分析",
                    "point_names": "1#综合污水排放口",
                    "point_count": 1,
                    "cycle_days": 1,
                    "frequency": "1次/天，连测1天",
                    "subtotal": 1260,
                    "items": [
                        {"id": 1, "item_name": "化学需氧量 (CODcr)", "category": "水和废水", "standard_code": "HJ 828-2017", "standard_name": "水质 化学需氧量的测定 重铬酸盐法", "method_name": "重铬酸盐回流滴定法", "one_order_one_pool": "是", "scene": "实验室分析", "need_subcontract": "否", "price": 150},
                        {"id": 3, "item_name": "氨氮 (NH3-N)", "category": "水和废水", "standard_code": "HJ 535-2009", "standard_name": "水质 氨氮的测定 纳氏试剂分光光度法", "method_name": "纳氏试剂分光光度法", "one_order_one_pool": "是", "scene": "实验室分析", "need_subcontract": "否", "price": 120},
                        {"id": 4, "item_name": "总磷 (TP)", "category": "水和废水", "standard_code": "GB/T 11893-1989", "standard_name": "水质 总磷的测定 钼酸铵分光光度法", "method_name": "过硫酸钾消解-钼酸铵光度法", "one_order_one_pool": "是", "scene": "实验室分析", "need_subcontract": "否", "price": 140},
                        {"id": 6, "item_name": "pH值", "category": "水和废水", "standard_code": "GB/T 6920-1986", "standard_name": "水质 pH值的测定 玻璃电极法", "method_name": "便携式玻璃电极法", "one_order_one_pool": "是", "scene": "现场直读", "need_subcontract": "否", "price": 40}
                    ]
                }
            ]''',
            status="已生效"
        )
        c2 = LimsContract(
            client_name="华东环境监测总站",
            client_address="上海市浦东新区张江高科园区龙东大道3000号",
            client_fax="021-50801234",
            client_contact="沈主任",
            client_email="shen@station-env.gov.cn",
            client_phone="13601234567",
            client_tel="021-50808888",
            tested_company="华东区域各重点河流采样断面",
            tested_address="长江口及太湖流域各重点入河排污断面",
            tested_fax="-",
            tested_contact="断面巡检组",
            tested_email="section@station-env.gov.cn",
            tested_phone="13701238888",
            tested_tel="021-50806666",
            contract_code="HT-20260910008",
            contract_name="长江中下游地表水质专项监测委托协议",
            biz_type="环境质量(HZ)",
            detection_type="水质检测",
            seal_type="CMA+CNAS双章",
            service_type="委托检测",
            sign_date="2026-09-10",
            start_date="2026-09-10",
            end_date="2026-12-31",
            sample_delivery_method="现场采样上门",
            accept_subcontract="否",
            is_judge="是",
            use_non_standard_method="否",
            salesman="李云",
            sample_disposal="实验室留样30天后无害化处理",
            discount=100.0,
            contract_amount=135680.0,
            total_amount_excl_tax=128000.0,
            quotation_code="BJ-20260910008",
            preset_approver="张总监",
            order_cs="赵琳",
            remark="重点断面COD、氨氮、总磷及重金属多参数全分析",
            one_order_one_pool="是",
            report_type="中文正式报告(带CMA章)",
            report_requirement="加急处理",
            report_count=4,
            report_fetch_method="派人自取",
            report_fetch_company="华东环境监测总站",
            report_fetch_address="上海市浦东新区张江高科园区龙东大道3000号",
            recipient_name="沈主任",
            recipient_phone="13601234567",
            invoice_type="增值税普通发票",
            invoice_title="华东环境监测总站",
            taxpayer_id="12310000425021008P",
            invoice_address="上海市浦东新区张江高科园区龙东大道3000号",
            invoice_phone="021-50808888",
            bank_name="中国工商银行上海张江支行",
            bank_account="1001223609006888999",
            monitoring_schemes='''[
                {
                    "detection_cycle": "定期监测",
                    "monitoring_type": "地表水检测",
                    "scheme_name": "长江重点断面地表水常规监测方案",
                    "point_names": "断面1#、断面2#、断面3#",
                    "point_count": 3,
                    "cycle_days": 1,
                    "frequency": "1次/月",
                    "subtotal": 38000,
                    "items": [
                        {"item_name": "高锰酸盐指数", "price": 80},
                        {"item_name": "氨氮 (NH3-N)", "price": 100},
                        {"item_name": "总磷 (TP)", "price": 110}
                    ]
                }
            ]''',
            status="已生效"
        )
        c3 = LimsContract(
            client_name="浙江蓝天环保新材料产业园",
            client_address="浙江省绍兴市柯桥区滨海工业区新材料大道88号",
            client_fax="0575-85551234",
            client_contact="马工",
            client_email="magong@bluesky.com",
            client_phone="13735123456",
            client_tel="0575-85556666",
            tested_company="浙江蓝天环保新材料产业园各排污口",
            tested_address="绍兴市柯桥区滨海工业区新材料大道88号1-3号车间",
            tested_fax="0575-85551234",
            tested_contact="环保科安全专员",
            tested_email="safety@bluesky.com",
            tested_phone="13735128888",
            tested_tel="0575-85557777",
            contract_code="HT-20260820015",
            contract_name="厂界无组织废气与恶臭污染物季度分析合同",
            biz_type="环境污染(HW)",
            detection_type="气体检测",
            seal_type="CMA资质印章",
            service_type="委托检测",
            sign_date="2026-08-20",
            start_date="2026-08-20",
            end_date="2027-08-19",
            sample_delivery_method="现场采样上门",
            accept_subcontract="是",
            is_judge="是",
            use_non_standard_method="否",
            salesman="陈经理",
            sample_disposal="气样现场分析，无保留",
            discount=90.0,
            contract_amount=65720.0,
            total_amount_excl_tax=62000.0,
            quotation_code="BJ-20260820015",
            preset_approver="王工",
            order_cs="周雪",
            remark="包含硫化氢、氨气及VOCs组分连续监测",
            one_order_one_pool="是",
            report_type="中文正式报告(带CMA章)",
            report_requirement="常规出具",
            report_count=2,
            report_fetch_method="顺丰快递到付",
            report_fetch_company="浙江蓝天环保新材料产业园",
            report_fetch_address="浙江省绍兴市柯桥区滨海工业区新材料大道88号办公楼",
            recipient_name="马工",
            recipient_phone="13735123456",
            invoice_type="增值税专用发票(6%)",
            invoice_title="浙江蓝天环保新材料股份有限公司",
            taxpayer_id="91330621712589632M",
            invoice_address="浙江省绍兴市柯桥区滨海工业区新材料大道88号",
            invoice_phone="0575-85556666",
            bank_name="中国农业银行绍兴柯桥支行",
            bank_account="19530101040008889",
            monitoring_schemes='''[
                {
                    "detection_cycle": "定期监测",
                    "monitoring_type": "废气有组织",
                    "scheme_name": "车间排气筒VOCs与恶臭监测方案",
                    "point_names": "1#烘干车间排气筒、2#喷涂车间排气筒",
                    "point_count": 2,
                    "cycle_days": 1,
                    "frequency": "1次/季",
                    "subtotal": 24000,
                    "items": [
                        {"item_name": "非甲烷总烃", "price": 280},
                        {"item_name": "硫化氢", "price": 160},
                        {"item_name": "氨气", "price": 140}
                    ]
                }
            ]''',
            status="已生效"
        )
        db.add_all([c1, c2, c3])
        db.commit()

    # 初始化审批任务数据（我的审批任务：报价审核、合同审核等）
    if db.query(LimsAuditTask).count() == 0:
        c_list = db.query(LimsContract).all()
        c1_id = c_list[0].id if len(c_list) > 0 else 1
        c2_id = c_list[1].id if len(c_list) > 1 else 2
        c3_id = c_list[2].id if len(c_list) > 2 else 3

        a1 = LimsAuditTask(
            business_code="BJ-20260901008",
            business_type="报价审批",
            business_name="浙江蓝天环保 - 工业园区排污口地表水及重金属季度监测报价",
            applicant="业务部-张伟",
            applicant_dept="环境咨询业务部",
            current_node="报价审核",
            current_handler="超级管理员",
            is_finished="否",
            audit_result="待审核",
            contract_id=c1_id,
            attachment_info=json.dumps([
                {"name": "报价清单与监测技术协议_v1.0.pdf", "size": "2.4 MB", "time": "2026-09-01 10:30", "uploader": "超级管理员"},
                {"name": "排污口现场勘验测点分布图.png", "size": "1.8 MB", "time": "2026-09-01 10:32", "uploader": "超级管理员"},
                {"name": "企业营业执照及环评批复副本.pdf", "size": "3.5 MB", "time": "2026-09-01 10:35", "uploader": "超级管理员"}
            ], ensure_ascii=False),
            audit_history=json.dumps([
                {
                    "node_name": "报价登记",
                    "operator": "超级管理员",
                    "action": "提交申请",
                    "operate_time": "2026-09-01 10:35:12",
                    "completion_status": "已完成",
                    "comment": "完成报价初稿及监测方案编制，提交进入报价审核流程"
                },
                {
                    "node_name": "报价审核",
                    "operator": "超级管理员",
                    "action": "待处理",
                    "operate_time": "-",
                    "completion_status": "处理中",
                    "comment": "等待审核人员查验合同、监测方案与费用核算"
                }
            ], ensure_ascii=False)
        )

        a2 = LimsAuditTask(
            business_code="BJ-20260910012",
            business_type="报价审批",
            business_name="苏州现代精细化工 - 综合污水处理厂进出水及污泥毒性检测报价",
            applicant="业务部-李晓华",
            applicant_dept="工业检测业务部",
            current_node="报价审核",
            current_handler="超级管理员",
            is_finished="否",
            audit_result="待审核",
            contract_id=c2_id,
            attachment_info=json.dumps([
                {"name": "精细化工污水毒性检测技术方案与报价核算单.xlsx", "size": "1.2 MB", "time": "2026-09-10 14:15", "uploader": "超级管理员"},
                {"name": "受检委托协议盖章扫描件.pdf", "size": "4.1 MB", "time": "2026-09-10 14:18", "uploader": "超级管理员"}
            ], ensure_ascii=False),
            audit_history=json.dumps([
                {
                    "node_name": "报价登记",
                    "operator": "超级管理员",
                    "action": "提交申请",
                    "operate_time": "2026-09-10 14:20:00",
                    "completion_status": "已完成",
                    "comment": "化工综合废水项目报价编制完成，提交审核"
                },
                {
                    "node_name": "报价审核",
                    "operator": "超级管理员",
                    "action": "待处理",
                    "operate_time": "-",
                    "completion_status": "处理中",
                    "comment": "等待审核人员复核"
                }
            ], ensure_ascii=False)
        )

        a3 = LimsAuditTask(
            business_code="HT-20260901006",
            business_type="合同审批",
            business_name="浙江蓝天环保新材料 - 厂界恶臭气体与VOCs年度检测合同",
            applicant="超级管理员",
            applicant_dept="市场业务部",
            current_node="合同录入",
            current_handler="超级管理员",
            is_finished="否",
            audit_result="待录入",
            contract_id=c3_id,
            attachment_info=json.dumps([
                {"name": "蓝天环保恶臭及VOCs年度检测委托协议初稿.docx", "size": "3.2 MB", "time": "2026-09-01 15:10", "uploader": "超级管理员"},
                {"name": "监测因子核算及资质证明文件.pdf", "size": "4.8 MB", "time": "2026-09-01 15:12", "uploader": "超级管理员"}
            ], ensure_ascii=False),
            audit_history=json.dumps([
                {
                    "node_name": "报价登记",
                    "operator": "超级管理员",
                    "action": "提交申请",
                    "operate_time": "2026-09-01 14:00:00",
                    "completion_status": "已完成",
                    "comment": "完成报价初稿及监测方案编制，提交进入报价审核流程"
                },
                {
                    "node_name": "报价审核",
                    "operator": "超级管理员",
                    "action": "同意",
                    "operate_time": "2026-09-01 15:00:00",
                    "completion_status": "已完成",
                    "comment": "报价与监测指标核算通过，同意流转进入合同细节录入完善"
                },
                {
                    "node_name": "合同录入",
                    "operator": "超级管理员",
                    "action": "待处理",
                    "operate_time": "-",
                    "completion_status": "处理中",
                    "comment": "等待完善合同条款、受检信息、监测方案及计费细节"
                }
            ], ensure_ascii=False)
        )

        a4 = LimsAuditTask(
            business_code="HT-20260825002",
            business_type="合同审批",
            business_name="杭州湾生态湿地保护区 - 季度水质及生态群落本底调查合同",
            applicant="超级管理员",
            applicant_dept="生态科研业务部",
            current_node="合同确认",
            current_handler="超级管理员",
            is_finished="否",
            audit_result="待确认",
            contract_id=c2_id,
            attachment_info=json.dumps([
                {"name": "生态湿地保护区监测技术协议与合同定稿.pdf", "size": "5.2 MB", "time": "2026-08-25 16:30", "uploader": "超级管理员"},
                {"name": "现场点位GPS定位图与采样方案.pdf", "size": "2.8 MB", "time": "2026-08-25 16:32", "uploader": "超级管理员"}
            ], ensure_ascii=False),
            audit_history=json.dumps([
                {
                    "node_name": "报价登记",
                    "operator": "超级管理员",
                    "action": "提交申请",
                    "operate_time": "2026-08-25 09:30:00",
                    "completion_status": "已完成",
                    "comment": "完成报价测算及方案初拟"
                },
                {
                    "node_name": "报价审核",
                    "operator": "超级管理员",
                    "action": "同意",
                    "operate_time": "2026-08-25 11:20:00",
                    "completion_status": "已完成",
                    "comment": "报价审核通过"
                },
                {
                    "node_name": "合同录入",
                    "operator": "超级管理员",
                    "action": "同意（完成录入）",
                    "operate_time": "2026-08-25 15:00:00",
                    "completion_status": "已完成",
                    "comment": "已核对并完善合同信息与监测方案细节，提交合同确认"
                },
                {
                    "node_name": "合同确认",
                    "operator": "超级管理员",
                    "action": "待处理",
                    "operate_time": "-",
                    "completion_status": "处理中",
                    "comment": "走合同确认流程，确认通过后将流转至委托下单节点"
                }
            ], ensure_ascii=False)
        )

        db.add_all([a1, a2, a3, a4])
        db.commit()

    db.close()
    print("[*] LIMS 核心数据库与初始演示数据初始化完成！")
