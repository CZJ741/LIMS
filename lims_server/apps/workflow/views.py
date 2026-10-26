from rest_framework.views import APIView
from django.contrib.contenttypes.models import ContentType
from common.responses import success_response, error_response
from .models import FlowActionLog
from .serializers import FlowActionLogSerializer

class FlowActionLogListView(APIView):
    """
    拉取指定业务对象的完整流转时间轴
    """
    def get(self, request):
        app_label = request.query_params.get('app_label')
        model = request.query_params.get('model')
        object_id = request.query_params.get('object_id')

        if not (app_label and model and object_id):
            return error_response("缺少必要参数: app_label, model, object_id")

        try:
            content_type = ContentType.objects.get(app_label=app_label, model=model)
        except ContentType.DoesNotExist:
            return error_response("找不到对应的业务实体模型")

        logs = FlowActionLog.objects.filter(
            content_type=content_type,
            object_id=object_id
        ).order_by('created_at')

        serializer = FlowActionLogSerializer(logs, many=True)
        return success_response(serializer.data)
