from rest_framework.views import APIView
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework_simplejwt.views import TokenObtainPairView
from common.responses import success_response
from .serializers import UserInfoSerializer
from .models import User

class CustomTokenObtainPairView(TokenObtainPairView):
    def post(self, request, *args, **kwargs):
        response = super().post(request, *args, **kwargs)
        if response.status_code == 200:
            user = User.objects.get(username=request.data.get('username'))
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
