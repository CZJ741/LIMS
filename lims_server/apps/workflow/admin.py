from django.contrib import admin
from .models import FlowActionLog

@admin.register(FlowActionLog)
class FlowActionLogAdmin(admin.ModelAdmin):
    list_display = ['action', 'content_type', 'object_id', 'from_status', 'to_status', 'operator_name', 'operator_role', 'created_at']
    list_filter = ['action', 'content_type', 'created_at']
    search_fields = ['object_id', 'operator_name', 'comment']
    readonly_fields = [f.name for f in FlowActionLog._meta.fields]

    def has_add_permission(self, request):
        return False

    def has_delete_permission(self, request, obj=None):
        return False
