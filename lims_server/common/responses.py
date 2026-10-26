from rest_framework.response import Response

def success_response(data=None, message="操作成功", code=200):
    return Response({
        "code": code,
        "message": message,
        "data": data if data is not None else {}
    }, status=code)

def error_response(message="操作失败", code=400, data=None):
    return Response({
        "code": code,
        "message": message,
        "data": data if data is not None else {}
    }, status=code)
