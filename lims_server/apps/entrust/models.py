from django.db import models
from django_fsm import FSMField, transition
from simple_history.models import HistoricalRecords
from common.models import BaseModel

class EntrustTask(BaseModel):
    """
    环境检测委托单任务实体（CMA/CNAS 标准范例）
    """
    STATUS_DRAFT = 'DRAFT'
    STATUS_SUBMITTED = 'SUBMITTED'
    STATUS_AUDITING = 'AUDITING'
    STATUS_ACCEPTED = 'ACCEPTED'
    STATUS_REJECTED = 'REJECTED'

    STATUS_CHOICES = (
        (STATUS_DRAFT, '草稿'),
        (STATUS_SUBMITTED, '已提交/待评审'),
        (STATUS_AUDITING, '评审中'),
        (STATUS_ACCEPTED, '受理通过'),
        (STATUS_REJECTED, '驳回修改'),
    )

    entrust_no = models.CharField(max_length=64, unique=True, verbose_name="委托单编号")
    client_name = models.CharField(max_length=128, verbose_name="委托单位")
    contact_person = models.CharField(max_length=64, verbose_name="联系人")
    contact_phone = models.CharField(max_length=32, verbose_name="联系电话")
    test_type = models.CharField(max_length=64, default="环境水质检测", verbose_name="检测类别")
    remarks = models.TextField(blank=True, null=True, verbose_name="委托备注")

    # 状态机受控字段
    status = FSMField(default=STATUS_DRAFT, choices=STATUS_CHOICES, protected=True, verbose_name="流转状态")

    # CMA/CNAS 合规变更追踪：自动记录字段级修改留痕快照
    history = HistoricalRecords()

    class Meta:
        verbose_name = "检测委托单"
        verbose_name_plural = verbose_name

    def __str__(self):
        return f"{self.entrust_no} - {self.client_name} ({self.get_status_display()})"

    @transition(field=status, source=STATUS_DRAFT, target=STATUS_SUBMITTED)
    def submit(self, operator, reason=""):
        """提交委托单评审"""
        pass

    @transition(field=status, source=STATUS_SUBMITTED, target=STATUS_AUDITING)
    def start_audit(self, operator, reason=""):
        """进入合规技术评审"""
        pass

    @transition(field=status, source=[STATUS_SUBMITTED, STATUS_AUDITING], target=STATUS_ACCEPTED)
    def accept(self, operator, reason="评审通过，准予受理"):
        """评审通过，正式受理任务"""
        pass

    @transition(field=status, source=[STATUS_SUBMITTED, STATUS_AUDITING], target=STATUS_REJECTED)
    def reject(self, operator, reason):
        """严格驳回机制：必须附带清晰的驳回原因"""
        if not reason or not reason.strip():
            raise ValueError("驳回操作必须填写明确的合规驳回原因")
