from rest_framework import permissions

class HasRolePermission(permissions.BasePermission):
    """
    基于用户绑定的职位/角色进行权限校验
    """
    required_roles = []

    def has_permission(self, request, view):
        if not request.user or not request.user.is_authenticated:
            return False
        if request.user.is_superuser:
            return True
        user_roles = request.user.roles.values_list('code', flat=True)
        if not self.required_roles:
            return True
        return any(role in user_roles for role in self.required_roles)
