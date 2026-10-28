from django.db import models
from common.models import BaseModel

class Customer(BaseModel):
    """
    客商/客户基本信息实体（符合 CMA/CNAS 合规溯源标准）
    用于委托单位、受检单位、开票单位的管理与业务追溯
    """
    # 基本信息
    name = models.CharField(max_length=255, verbose_name="客户名称")
    legal_person = models.CharField(max_length=100, blank=True, default="", verbose_name="法人代表")
    address = models.CharField(max_length=500, blank=True, default="", verbose_name="通讯地址")
    contact_person = models.CharField(max_length=100, blank=True, default="", verbose_name="联系人")
    mobile = models.CharField(max_length=50, blank=True, default="", verbose_name="手机")
    phone = models.CharField(max_length=50, blank=True, default="", verbose_name="固定电话")
    email = models.CharField(max_length=100, blank=True, default="", verbose_name="邮箱")
    fax = models.CharField(max_length=50, blank=True, default="", verbose_name="传真")

    # 开票信息
    tax_title = models.CharField(max_length=255, blank=True, default="", verbose_name="发票抬头")
    tax_number = models.CharField(max_length=100, blank=True, default="", verbose_name="纳税人识别号")
    invoice_address = models.CharField(max_length=500, blank=True, default="", verbose_name="开票地址")
    invoice_phone = models.CharField(max_length=50, blank=True, default="", verbose_name="开票电话")
    bank_name = models.CharField(max_length=200, blank=True, default="", verbose_name="开户行")
    bank_account = models.CharField(max_length=100, blank=True, default="", verbose_name="银行账号")

    # 备注与扩展
    remarks = models.TextField(blank=True, default="", verbose_name="备注")

    class Meta:
        db_table = "lims_customer"
        verbose_name = "客户基本信息"
        verbose_name_plural = verbose_name
        ordering = ["-created_at"]

    def __str__(self):
        return self.name
