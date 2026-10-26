from django.db import transaction
from django.contrib.contenttypes.models import ContentType
from .models import FlowActionLog

class WorkflowDispatcher:
    """
    通用工作流调度中心：负责执行状态转换与记录 CMA/CNAS 合规流水
    """

    @classmethod
    @transaction.atomic
    def execute_transition(cls, instance, transition_method_name, operator, comment="", **kwargs):
        """
        调度并执行 FSM transition，原子级记录日志
        """
        from_status = instance.status

        # 检查并调用 FSM 状态机方法
        transition_method = getattr(instance, transition_method_name, None)
        if not transition_method or not callable(transition_method):
            raise ValueError(f"目标模型未定义流转动作方法: {transition_method_name}")

        # 执行转换
        transition_method(operator=operator, reason=comment, **kwargs)
        instance.save()
        to_status = instance.status

        # 解析动作类型
        action = FlowActionLog.ACTION_APPROVE
        if 'reject' in transition_method_name.lower():
            action = FlowActionLog.ACTION_REJECT
            if not comment.strip():
                raise ValueError("审批驳回必须填写驳回原因")
        elif 'submit' in transition_method_name.lower():
            action = FlowActionLog.ACTION_SUBMIT
        elif 'withdraw' in transition_method_name.lower():
            action = FlowActionLog.ACTION_WITHDRAW

        # 获取角色快照
        roles = list(operator.roles.values_list('name', flat=True)) if hasattr(operator, 'roles') else []
        role_snapshot = " / ".join(roles) if roles else ("管理员" if operator.is_superuser else "经办人")

        # 插入合规留痕流水
        content_type = ContentType.objects.get_for_model(instance)
        log = FlowActionLog.objects.create(
            content_type=content_type,
            object_id=str(instance.id),
            action=action,
            from_status=str(from_status),
            to_status=str(to_status),
            operator=operator,
            operator_name=operator.real_name or operator.username,
            operator_role=role_snapshot,
            comment=comment or "审核通过"
        )
        return log
