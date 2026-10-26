from django.db import models
from django.conf import settings
from django.contrib.contenttypes.models import ContentType
from django.contrib.contenttypes.fields import GenericForeignKey
from common.models import BaseModel

class FlowActionLog(BaseModel):
    """
    CMA/CNAS 核心合规流转流水日志：
    记录任何业务实体（合同、委托单、原始记录、报告）的流转痕迹
    """
    ACTION_SUBMIT = 'SUBMIT'
    ACTION_APPROVE = 'APPROVE'
    ACTION_REJECT = 'REJECT'
    ACTION_WITHDRAW = 'WITHDRAW'
    ACTION_TERMINATE = 'TERMINATE'

    ACTION_CHOICES = (
        (ACTION_SUBMIT, '提交审核'),
        (ACTION_APPROVE, '审核通过'),
        (ACTION_REJECT, '审批驳回'),
        (ACTION_WITHDRAW, '主动撤回'),
        (ACTION_TERMINATE, '异常终止'),
    )

    content_type = models.ForeignKey(ContentType, on_delete=models.CASCADE, verbose_name="关联业务模型")
    object_id = models.CharField(max_length=64, verbose_name="关联业务对象ID")
    target_object = GenericForeignKey('content_type', 'object_id')

    action = models.CharField(max_length=32, choices=ACTION_CHOICES, verbose_name="操作动作")
    from_status = models.CharField(max_length=64, verbose_name="前置状态")
    to_status = models.CharField(max_length=64, verbose_name="目标状态")

    operator = models.ForeignKey(
        settings.AUTH_USER_MODEL, on_delete=models.PROTECT,
        related_name="flow_action_logs", verbose_name="操作人员"
    )
    operator_name = models.CharField(max_length=64, verbose_name="操作人姓名快照")
    operator_role = models.CharField(max_length=64, blank=True, null=True, verbose_name="操作时职位角色")

    comment = models.TextField(verbose_name="审批批注/驳回原因")

    class Meta:
        verbose_name = "审批留痕流水"
        verbose_name_plural = verbose_name
        ordering = ['-created_at']

    def __str__(self):
        return f"[{self.action}] {self.content_type.model} ({self.object_id}): {self.from_status} -> {self.to_status} by {self.operator_name}"
