from rest_framework import serializers
from .models import User, Department, Role

class RoleSerializer(serializers.ModelSerializer):
    class Meta:
        model = Role
        fields = ['id', 'name', 'code', 'description']

class DepartmentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Department
        fields = ['id', 'name', 'code', 'description']

class UserInfoSerializer(serializers.ModelSerializer):
    department_name = serializers.CharField(source='department.name', read_only=True)
    roles = RoleSerializer(many=True, read_only=True)
    role_codes = serializers.SerializerMethodField()

    class Meta:
        model = User
        fields = ['id', 'username', 'real_name', 'email', 'phone', 'department', 'department_name', 'roles', 'role_codes', 'signature_image']

    def get_role_codes(self, obj):
        return list(obj.roles.values_list('code', flat=True))
