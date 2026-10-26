import uuid
from django.db import models
from django.conf import settings

class SoftDeleteQuerySet(models.QuerySet):
    def delete(self):
        return super().update(is_deleted=True)

    def hard_delete(self):
        return super().delete()

    def alive(self):
        return self.filter(is_deleted=False)

    def dead(self):
        return self.filter(is_deleted=True)

class SoftDeleteManager(models.Manager):
    def get_queryset(self):
        return SoftDeleteQuerySet(self.model, using=self._db).filter(is_deleted=False)

    def all_with_deleted(self):
        return SoftDeleteQuerySet(self.model, using=self._db)

class BaseModel(models.Model):
    """
    环境检测 LIMS 核心通用实体基类：
    1. UUID 主键避免主键泄露与连续性猜测
    2. 全生命周期创建/修改时间留痕
    3. 软删除支持（CMA/CNAS 原始数据严禁随意硬物理删除）
    """
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False, verbose_name="主键UUID")
    created_at = models.DateTimeField(auto_now_add=True, verbose_name="创建时间")
    updated_at = models.DateTimeField(auto_now=True, verbose_name="更新时间")
    created_by = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name="%(class)s_created",
        verbose_name="创建人"
    )
    is_deleted = models.BooleanField(default=False, verbose_name="是否已删除")

    objects = SoftDeleteManager()
    all_objects = models.Manager()

    class Meta:
        abstract = True
        ordering = ['-created_at']

    def delete(self, using=None, keep_parents=False):
        self.is_deleted = True
        self.save(update_fields=['is_deleted'])
