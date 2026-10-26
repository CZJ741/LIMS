import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'config.settings')
django.setup()

from apps.authentication.models import User, Department, Role

dept, _ = Department.objects.get_or_create(
    code='ADMIN_DEPT',
    defaults={'name': '系统总控中心', 'description': 'LIMS 系统最高运维管理部门'}
)

role, _ = Role.objects.get_or_create(
    code='ADMIN',
    defaults={'name': '超级管理员', 'description': '全系统所有资源最高配置与调度权限'}
)

if not User.objects.filter(username='admin').exists():
    user = User.objects.create_superuser(
        username='admin',
        password='123456',
        real_name='系统超级管理员',
        department=dept
    )
    user.roles.add(role)
    print("SUCCESS: superuser 'admin' created with password '123456'.")
else:
    user = User.objects.get(username='admin')
    user.set_password('123456')
    user.real_name = '系统超级管理员'
    user.department = dept
    user.is_superuser = True
    user.is_staff = True
    user.save()
    user.roles.add(role)
    print("SUCCESS: superuser 'admin' updated with password '123456'.")
