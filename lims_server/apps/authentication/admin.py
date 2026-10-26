from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from .models import User, Department, Role

@admin.register(User)
class UserAdmin(BaseUserAdmin):
    list_display = ['username', 'real_name', 'phone', 'department', 'is_staff', 'is_active']
    fieldsets = BaseUserAdmin.fieldsets + (
        ('LIMS 业务属性', {'fields': ('real_name', 'phone', 'department', 'roles', 'signature_image')}),
    )

@admin.register(Department)
class DepartmentAdmin(admin.ModelAdmin):
    list_display = ['name', 'code', 'created_at']
    search_fields = ['name', 'code']

@admin.register(Role)
class RoleAdmin(admin.ModelAdmin):
    list_display = ['name', 'code', 'created_at']
    search_fields = ['name', 'code']
