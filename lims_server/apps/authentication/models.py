from django.db import models
from django.contrib.auth.models import AbstractUser
from common.models import BaseModel

class Department(BaseModel):
    ORG_TYPE_CHOICES = (
        ('COMPANY', '单位'),
        ('BRANCH', '分支机构'),
        ('DEPT', '部门'),
        ('LAB', '检测实验室'),
        ('TEAM', '采样/业务组'),
    )
    BUSINESS_TYPE_CHOICES = (
        ('ENV', '环境检测'),
        ('OCCUPATIONAL', '职业卫生'),
        ('PUBLIC_HEALTH', '公共卫生'),
        ('COMPREHENSIVE', '综合业务'),
        ('OTHER', '其他'),
    )

    name = models.CharField(max_length=64, verbose_name="单位/部门名称")
    code = models.CharField(max_length=32, unique=True, verbose_name="单位编码")
    parent = models.ForeignKey(
        'self',
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='children',
        verbose_name="上级单位"
    )
    org_type = models.CharField(
        max_length=20,
        choices=ORG_TYPE_CHOICES,
        default='COMPANY',
        verbose_name="单位类型"
    )
    business_type = models.CharField(
        max_length=20,
        choices=BUSINESS_TYPE_CHOICES,
        default='ENV',
        verbose_name="业务类型"
    )
    is_legal_person = models.BooleanField(default=False, verbose_name="是否独立法人")
    company_name = models.CharField(max_length=128, blank=True, null=True, verbose_name="所属单位名称")
    sort_order = models.IntegerField(default=0, verbose_name="排序号")
    is_active = models.BooleanField(default=True, verbose_name="是否有效")
    description = models.TextField(blank=True, null=True, verbose_name="职责描述")

    class Meta:
        verbose_name = "组织部门"
        verbose_name_plural = verbose_name
        ordering = ['sort_order', 'code']

    def delete(self, *args, **kwargs):
        # 1. 核心管理员部门保护
        if self.code == 'ADMIN_DEPT':
            from django.core.exceptions import PermissionDenied
            raise PermissionDenied("系统总控核心部门受最高保护，严禁删除！")
        # 2. 检查下级子单位关联
        if self.children.exists():
            from django.core.exceptions import PermissionDenied
            raise PermissionDenied(f"单位【{self.name}】下存在子级组织/部门，请先调整或清理下级单位后方可操作！")
        # 3. 检查人员关联
        if hasattr(self, 'users') and self.users.exists():
            from django.core.exceptions import PermissionDenied
            raise PermissionDenied(f"单位【{self.name}】下存在在职人员，为保证审计追溯闭环，严禁直接删除！")
        return super().delete(*args, **kwargs)

    def __str__(self):
        return f"{self.name} ({self.code})"

class Role(BaseModel):
    name = models.CharField(max_length=64, unique=True, verbose_name="职位/角色名称")
    code = models.CharField(max_length=32, unique=True, verbose_name="角色编码")
    sort_order = models.IntegerField(default=0, verbose_name="排序号")
    description = models.TextField(blank=True, null=True, verbose_name="备注/权限描述")
    is_active = models.BooleanField(default=True, verbose_name="是否启用")
    menu_permissions = models.JSONField(default=list, blank=True, verbose_name="菜单权限键列表")

    def delete(self, *args, **kwargs):
        if self.code == 'ADMIN':
            from django.core.exceptions import PermissionDenied
            raise PermissionDenied("超级管理员核心角色受系统保护，严禁删除！")
        return super().delete(*args, **kwargs)

    class Meta:
        verbose_name = "职位角色"
        verbose_name_plural = verbose_name
        ordering = ['sort_order', 'created_at']

    def __str__(self):
        return f"{self.name} ({self.code})"

class User(AbstractUser):
    GENDER_CHOICES = (
        ('male', '男'),
        ('female', '女'),
        ('other', '其他'),
    )

    user_code = models.CharField(max_length=32, unique=True, null=True, blank=True, verbose_name="人员编码")
    real_name = models.CharField(max_length=64, blank=True, verbose_name="人员姓名")
    company = models.CharField(max_length=128, blank=True, default="", verbose_name="所属单位")
    gender = models.CharField(max_length=16, choices=GENDER_CHOICES, default='male', verbose_name="性别")
    id_card = models.CharField(max_length=18, blank=True, default="", verbose_name="身份证号码")
    employee_no = models.CharField(max_length=32, blank=True, default="", verbose_name="工号")
    phone = models.CharField(max_length=20, blank=True, verbose_name="电话号码")
    birth_date = models.DateField(null=True, blank=True, verbose_name="出生年月")
    job_title = models.CharField(max_length=64, blank=True, default="", verbose_name="职务/职称")
    education = models.CharField(max_length=32, blank=True, default="", verbose_name="学历学位")
    graduated_school = models.CharField(max_length=128, blank=True, default="", verbose_name="毕业院校")
    major = models.CharField(max_length=64, blank=True, default="", verbose_name="所学专业")
    work_start_date = models.DateField(null=True, blank=True, verbose_name="参加工作时间")
    years_in_position = models.DecimalField(max_digits=4, decimal_places=1, null=True, blank=True, verbose_name="从事本岗位年限")
    training_experience = models.TextField(blank=True, default="", verbose_name="培训经历")
    work_experience = models.TextField(blank=True, default="", verbose_name="工作经历")
    residence_area = models.CharField(max_length=256, blank=True, default="", verbose_name="常住地")

    department = models.ForeignKey(
        Department, on_delete=models.SET_NULL, null=True, blank=True,
        related_name="members", verbose_name="所属部门"
    )
    roles = models.ManyToManyField(Role, blank=True, related_name="users", verbose_name="担任角色")
    signature_image = models.ImageField(
        upload_to="signatures/", null=True, blank=True, verbose_name="电子手写签名图片"
    )

    def delete(self, *args, **kwargs):
        if self.is_superuser or self.username == 'admin':
            from django.core.exceptions import PermissionDenied
            raise PermissionDenied("系统超级管理员受核心安全保护，严禁删除！")
        return super().delete(*args, **kwargs)

    class Meta:
        verbose_name = "检测业务人员"
        verbose_name_plural = verbose_name

    def __str__(self):
        return f"{self.real_name or self.username} ({self.username})"
