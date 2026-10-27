from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from .models import User, Department, Role

@admin.register(User)
class UserAdmin(BaseUserAdmin):
    list_display = ['username', 'user_code', 'real_name', 'department', 'job_title', 'phone', 'is_staff', 'is_active']
    search_fields = ['username', 'user_code', 'real_name', 'phone', 'employee_no', 'id_card']
    fieldsets = BaseUserAdmin.fieldsets + (
        ('LIMS 人员档案与业务属性', {
            'fields': (
                'user_code', 'real_name', 'department', 'company', 'gender', 'id_card',
                'employee_no', 'phone', 'birth_date', 'job_title', 'education',
                'graduated_school', 'major', 'work_start_date', 'years_in_position',
                'training_experience', 'work_experience', 'residence_area',
                'roles', 'signature_image'
            )
        }),
    )

@admin.register(Department)
class DepartmentAdmin(admin.ModelAdmin):
    list_display = ['name', 'code', 'created_at']
    search_fields = ['name', 'code']

@admin.register(Role)
class RoleAdmin(admin.ModelAdmin):
    list_display = ['name', 'code', 'created_at']
    search_fields = ['name', 'code']
