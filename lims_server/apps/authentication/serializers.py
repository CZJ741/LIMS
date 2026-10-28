from rest_framework import serializers
from .models import User, Department, Role

class RoleSerializer(serializers.ModelSerializer):
    class Meta:
        model = Role
        fields = ['id', 'name', 'code', 'sort_order', 'description', 'is_active', 'menu_permissions', 'created_at']
        extra_kwargs = {
            'code': {'required': False},
            'sort_order': {'required': False, 'default': 0},
            'is_active': {'required': False, 'default': True},
            'menu_permissions': {'required': False}
        }

    def validate_code(self, value):
        if value:
            qs = Role.objects.filter(code=value)
            if self.instance:
                qs = qs.exclude(pk=self.instance.pk)
            if qs.exists():
                raise serializers.ValidationError(f"角色编码【{value}】已存在，请勿重复输入")
        return value

    def validate_name(self, value):
        if not value:
            raise serializers.ValidationError("角色名称不能为空")
        qs = Role.objects.filter(name=value)
        if self.instance:
            qs = qs.exclude(pk=self.instance.pk)
        if qs.exists():
            raise serializers.ValidationError(f"角色名称【{value}】已存在，请勿重复命名")
        return value

class DepartmentSerializer(serializers.ModelSerializer):
    parent_name = serializers.CharField(source='parent.name', read_only=True)
    org_type_display = serializers.CharField(source='get_org_type_display', read_only=True)
    business_type_display = serializers.CharField(source='get_business_type_display', read_only=True)
    children = serializers.SerializerMethodField()

    class Meta:
        model = Department
        fields = [
            'id', 'name', 'code', 'parent', 'parent_name',
            'org_type', 'org_type_display',
            'business_type', 'business_type_display',
            'is_legal_person', 'company_name',
            'sort_order', 'is_active', 'description',
            'children', 'created_at'
        ]

    def get_children(self, obj):
        # 仅在非嵌套序列化或单项时动态读取
        children = obj.children.filter(is_active=True).order_by('sort_order', 'code')
        if not children.exists():
            return None
        return DepartmentSerializer(children, many=True).data

    def validate_code(self, value):
        if not value:
            raise serializers.ValidationError("单位编码不能为空")
        qs = Department.objects.filter(code=value)
        if self.instance:
            qs = qs.exclude(pk=self.instance.pk)
        if qs.exists():
            raise serializers.ValidationError(f"单位编码【{value}】已存在，请勿重复输入")
        return value

    def validate(self, attrs):
        parent = attrs.get('parent')
        if self.instance and parent:
            if parent.pk == self.instance.pk:
                raise serializers.ValidationError({"parent": "上级单位不能选择当前单位本身"})
        return attrs

class UserInfoSerializer(serializers.ModelSerializer):
    department_name = serializers.CharField(source='department.name', read_only=True)
    roles = RoleSerializer(many=True, read_only=True)
    role_codes = serializers.SerializerMethodField()

    class Meta:
        model = User
        fields = [
            'id', 'username', 'user_code', 'real_name', 'email', 'phone',
            'company', 'gender', 'id_card', 'employee_no', 'birth_date',
            'job_title', 'education', 'graduated_school', 'major',
            'work_start_date', 'years_in_position', 'training_experience',
            'work_experience', 'residence_area',
            'department', 'department_name', 'roles', 'role_codes', 'signature_image',
            'last_login'
        ]

    def get_role_codes(self, obj):
        return list(obj.roles.values_list('code', flat=True))


class UserManageSerializer(serializers.ModelSerializer):
    department_name = serializers.CharField(source='department.name', read_only=True)
    roles_info = RoleSerializer(source='roles', many=True, read_only=True)

    class Meta:
        model = User
        fields = [
            'id', 'username', 'user_code', 'real_name', 'email', 'phone',
            'company', 'gender', 'id_card', 'employee_no', 'birth_date',
            'job_title', 'education', 'graduated_school', 'major',
            'work_start_date', 'years_in_position', 'training_experience',
            'work_experience', 'residence_area',
            'department', 'department_name', 'roles', 'roles_info', 'signature_image',
            'is_active', 'last_login', 'date_joined'
        ]
        extra_kwargs = {
            'username': {'required': False, 'allow_blank': True},
            'user_code': {'required': True}
        }

    def validate_user_code(self, value):
        if not value:
            raise serializers.ValidationError("人员编码不能为空")
        qs = User.objects.filter(user_code=value)
        if self.instance:
            qs = qs.exclude(pk=self.instance.pk)
        if qs.exists():
            raise serializers.ValidationError(f"人员编码【{value}】已存在，请勿重复输入")
        return value

    def create(self, validated_data):
        user_code = validated_data.get('user_code')
        username = validated_data.get('username')
        if not username:
            username = user_code
            validated_data['username'] = username

        # 确保 username 也唯一，若同名则追加后缀
        base_username = username
        counter = 1
        while User.objects.filter(username=username).exists():
            username = f"{base_username}_{counter}"
            counter += 1
        validated_data['username'] = username

        roles = validated_data.pop('roles', [])
        user = User(**validated_data)
        user.set_password('123456')
        user.save()
        if roles:
            user.roles.set(roles)
        return user
