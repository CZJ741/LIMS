from django.db import models
from django.contrib.auth.models import AbstractUser
from common.models import BaseModel

class Department(BaseModel):
    name = models.CharField(max_length=64, unique=True, verbose_name="部门名称")
    code = models.CharField(max_length=32, unique=True, verbose_name="部门编码")
    description = models.TextField(blank=True, null=True, verbose_name="部门职责")

    class Meta:
        verbose_name = "组织部门"
        verbose_name_plural = verbose_name

    def __str__(self):
        return self.name

class Role(BaseModel):
    name = models.CharField(max_length=64, unique=True, verbose_name="职位/角色名称")
    code = models.CharField(max_length=32, unique=True, verbose_name="角色编码")
    description = models.TextField(blank=True, null=True, verbose_name="权限描述")

    class Meta:
        verbose_name = "职位角色"
        verbose_name_plural = verbose_name

    def __str__(self):
        return f"{self.name} ({self.code})"

class User(AbstractUser):
    real_name = models.CharField(max_length=64, blank=True, verbose_name="真实姓名")
    phone = models.CharField(max_length=20, blank=True, verbose_name="联系电话")
    department = models.ForeignKey(
        Department, on_delete=models.SET_NULL, null=True, blank=True,
        related_name="members", verbose_name="所属部门"
    )
    roles = models.ManyToManyField(Role, blank=True, related_name="users", verbose_name="担任角色")
    signature_image = models.ImageField(
        upload_to="signatures/", null=True, blank=True, verbose_name="电子手写签名图片"
    )

    class Meta:
        verbose_name = "检测业务人员"
        verbose_name_plural = verbose_name

    def __str__(self):
        return f"{self.real_name or self.username} ({self.username})"
