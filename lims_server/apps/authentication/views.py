from rest_framework import viewsets, filters, status
from rest_framework.views import APIView
from rest_framework.decorators import action
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework_simplejwt.views import TokenObtainPairView
from django.contrib.auth.models import update_last_login
from common.responses import success_response, error_response
from .serializers import UserInfoSerializer, UserManageSerializer, DepartmentSerializer, RoleSerializer
from .models import User, Department, Role

class CustomTokenObtainPairView(TokenObtainPairView):
    def post(self, request, *args, **kwargs):
        response = super().post(request, *args, **kwargs)
        if response.status_code == 200:
            user = User.objects.get(username=request.data.get('username'))
            update_last_login(None, user)
            user_data = UserInfoSerializer(user).data
            return success_response({
                "access": response.data.get('access'),
                "refresh": response.data.get('refresh'),
                "user": user_data
            }, message="登录成功")
        return response

class CurrentUserView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        serializer = UserInfoSerializer(request.user)
        return success_response(serializer.data)


class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all().select_related('department').order_by('-date_joined')
    serializer_class = UserManageSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        qs = super().get_queryset()
        user_code = self.request.query_params.get('user_code')
        real_name = self.request.query_params.get('real_name')
        phone = self.request.query_params.get('phone')
        email = self.request.query_params.get('email')
        department = self.request.query_params.get('department')
        company = self.request.query_params.get('company')

        if user_code:
            qs = qs.filter(user_code__icontains=user_code.strip())
        if real_name:
            qs = qs.filter(real_name__icontains=real_name.strip())
        if phone:
            qs = qs.filter(phone__icontains=phone.strip())
        if email:
            qs = qs.filter(email__icontains=email.strip())
        if department:
            qs = qs.filter(department_id=department)
        if company:
            qs = qs.filter(company__icontains=company.strip())
        return qs

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        self.perform_create(serializer)
        return success_response(serializer.data, message="人员创建成功", code=status.HTTP_201_CREATED)

    def list(self, request, *args, **kwargs):
        queryset = self.filter_queryset(self.get_queryset())
        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)
        serializer = self.get_serializer(queryset, many=True)
        return success_response(serializer.data)

    def update(self, request, *args, **kwargs):
        partial = kwargs.pop('partial', request.method == 'PATCH')
        instance = self.get_object()
        if (instance.is_superuser or instance.username == 'admin') and 'is_active' in request.data:
            if not request.data['is_active']:
                return error_response(message="系统超级管理员账号受核心安全保护，严禁停用！", code=status.HTTP_400_BAD_REQUEST)
        serializer = self.get_serializer(instance, data=request.data, partial=partial)
        serializer.is_valid(raise_exception=True)
        self.perform_update(serializer)
        return success_response(serializer.data, message="人员更新成功")

    @action(detail=True, methods=['post'], url_path='reset-password')
    def reset_password(self, request, pk=None):
        instance = self.get_object()
        # 超级管理员密码保护，严禁非超管用户随意重置
        if (instance.is_superuser or instance.username == 'admin') and request.user.username != 'admin':
            return error_response(message="系统超级管理员密码受核心保护，仅允许超管本人维护！", code=status.HTTP_403_FORBIDDEN)

        new_password = request.data.get('new_password')
        if not new_password or not str(new_password).strip():
            new_password = '123456'
        else:
            new_password = str(new_password).strip()

        if len(new_password) < 6:
            return error_response(message="新密码长度不能少于6位字符", code=status.HTTP_400_BAD_REQUEST)

        instance.set_password(new_password)
        instance.save(update_fields=['password'])
        return success_response({
            "default_password": new_password
        }, message=f"人员【{instance.real_name or instance.username}】密码重置成功")

    @action(detail=True, methods=['post'], url_path='assign-roles')
    def assign_roles(self, request, pk=None):
        instance = self.get_object()
        role_ids = request.data.get('roles', [])
        if not isinstance(role_ids, list):
            return error_response(message="角色列表格式有误，必须为数组格式", code=status.HTTP_400_BAD_REQUEST)

        roles = Role.objects.filter(id__in=role_ids, is_active=True)
        instance.roles.set(roles)
        serializer = self.get_serializer(instance)
        return success_response(serializer.data, message=f"人员【{instance.real_name or instance.username}】职务配置成功")

    def destroy(self, request, *args, **kwargs):
        instance = self.get_object()
        if instance.is_superuser or instance.username == 'admin':
            return error_response(message="系统超级管理员账号受核心安全保护，严禁删除！", code=status.HTTP_400_BAD_REQUEST)
        self.perform_destroy(instance)
        return success_response(None, message="人员删除成功")


class DepartmentViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Department.objects.all().order_by('name')
    serializer_class = DepartmentSerializer
    permission_classes = [IsAuthenticated]
    pagination_class = None

    def list(self, request, *args, **kwargs):
        serializer = self.get_serializer(self.get_queryset(), many=True)
        return success_response(serializer.data)


class RoleViewSet(viewsets.ModelViewSet):
    queryset = Role.objects.all().order_by('sort_order', 'code')
    serializer_class = RoleSerializer
    permission_classes = [IsAuthenticated]
    pagination_class = None

    def create(self, request, *args, **kwargs):
        data = request.data.copy()
        # 如果未传入角色编码，则自动生成简洁纯数字编码（起始 10001 自增）
        if not data.get('code'):
            code_candidates = Role.objects.filter(code__regex=r'^\d+$').values_list('code', flat=True)
            numeric_codes = [int(c) for c in code_candidates if c.isdigit()]
            next_code = max(numeric_codes) + 1 if numeric_codes else 10001
            while Role.objects.filter(code=str(next_code)).exists():
                next_code += 1
            data['code'] = str(next_code)
        serializer = self.get_serializer(data=data)
        serializer.is_valid(raise_exception=True)
        self.perform_create(serializer)
        return success_response(serializer.data, message="角色创建成功", code=status.HTTP_201_CREATED)

    def update(self, request, *args, **kwargs):
        partial = kwargs.pop('partial', request.method == 'PATCH')
        instance = self.get_object()
        if instance.code == 'ADMIN' and 'is_active' in request.data:
            if not request.data['is_active']:
                return error_response(message="系统超级管理员核心角色受保护，严禁停用！", code=status.HTTP_400_BAD_REQUEST)
        serializer = self.get_serializer(instance, data=request.data, partial=partial)
        serializer.is_valid(raise_exception=True)
        self.perform_update(serializer)
        return success_response(serializer.data, message="角色更新成功")

    def list(self, request, *args, **kwargs):
        serializer = self.get_serializer(self.get_queryset(), many=True)
        return success_response(serializer.data)

    def destroy(self, request, *args, **kwargs):
        instance = self.get_object()
        if instance.code == 'ADMIN':
            return error_response(message="系统超级管理员核心角色受保护，严禁删除！", code=status.HTTP_400_BAD_REQUEST)
        # 硬删除：直接调用 models.Model 原生物理删除，彻底清理数据及编码占用
        from django.db import models as dj_models
        dj_models.Model.delete(instance)
        return success_response(None, message="角色删除成功")
