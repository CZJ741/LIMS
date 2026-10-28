from rest_framework import viewsets, permissions, status
from rest_framework.response import Response
from rest_framework.decorators import action
from django.db.models import Q
from .models import Customer
from .serializers import CustomerSerializer

class CustomerViewSet(viewsets.ModelViewSet):
    """
    客商管理 - 客户基本信息 CRUD 视图集
    """
    queryset = Customer.objects.all()
    serializer_class = CustomerSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        qs = Customer.objects.all()
        params = self.request.query_params

        name = params.get('name', '').strip()
        address = params.get('address', '').strip()
        contact_person = params.get('contact_person', '').strip()
        phone = params.get('phone', '').strip()

        if name:
            qs = qs.filter(name__icontains=name)
        if address:
            qs = qs.filter(address__icontains=address)
        if contact_person:
            qs = qs.filter(contact_person__icontains=contact_person)
        if phone:
            # 兼容固定电话或手机检索
            qs = qs.filter(Q(phone__icontains=phone) | Q(mobile__icontains=phone))

        return qs

    def perform_create(self, serializer):
        serializer.save(created_by=self.request.user)

    def destroy(self, request, *args, **kwargs):
        instance = self.get_object()
        # 软删除
        instance.delete()
        return Response({"message": "客户已安全移入历史归档"}, status=status.HTTP_200_OK)

    @action(detail=False, methods=['post'], url_path='batch-delete')
    def batch_delete(self, request):
        ids = request.data.get('ids', [])
        if not ids or not isinstance(ids, list):
            return Response({"message": "请选择要删除的客户记录"}, status=status.HTTP_400_BAD_REQUEST)

        count = Customer.objects.filter(id__in=ids).count()
        if count == 0:
            return Response({"message": "未找到匹配的客户记录"}, status=status.HTTP_404_NOT_FOUND)

        Customer.objects.filter(id__in=ids).delete()
        return Response({"message": f"成功批量删除 {count} 条客户记录"}, status=status.HTTP_200_OK)
