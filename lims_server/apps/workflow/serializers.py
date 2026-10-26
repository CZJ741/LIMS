from rest_framework import serializers
from .models import FlowActionLog

class FlowActionLogSerializer(serializers.ModelSerializer):
    created_at_display = serializers.DateTimeField(source='created_at', format='%Y-%m-%d %H:%M:%S', read_only=True)

    class Meta:
        model = FlowActionLog
        fields = [
            'id', 'action', 'from_status', 'to_status',
            'operator_name', 'operator_role', 'comment',
            'created_at', 'created_at_display'
        ]
