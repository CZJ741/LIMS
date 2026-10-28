from rest_framework import serializers
from .models import Customer

class CustomerSerializer(serializers.ModelSerializer):
    """
    客户基本信息序列化器
    """
    created_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S", read_only=True)
    updated_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S", read_only=True)

    class Meta:
        model = Customer
        fields = [
            'id',
            'name',
            'legal_person',
            'address',
            'contact_person',
            'mobile',
            'phone',
            'email',
            'fax',
            'tax_title',
            'tax_number',
            'invoice_address',
            'invoice_phone',
            'bank_name',
            'bank_account',
            'remarks',
            'created_at',
            'updated_at',
        ]
        read_only_fields = ['id', 'created_at', 'updated_at']

    def validate_name(self, value):
        val = value.strip()
        if not val:
            raise serializers.ValidationError("客户名称不能为空")

        # 校验同一有效状态下客户名称是否重复
        qs = Customer.objects.filter(name=val)
        if self.instance:
            qs = qs.exclude(pk=self.instance.pk)
        if qs.exists():
            raise serializers.ValidationError(f"客户名称【{val}】已存在，请勿重复创建")
        return val
