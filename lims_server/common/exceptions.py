from rest_framework.views import exception_handler
from rest_framework.response import Response
from rest_framework import status

def _extract_detail(data):
    """
    递归提取 DRF ValidationError 中的具体报错文本
    """
    if isinstance(data, list):
        if len(data) > 0:
            return _extract_detail(data[0])
        return "数据校验错误"
    elif isinstance(data, dict):
        # 遍历键值提取第一个最具代表性的错误描述
        messages = []
        for key, val in data.items():
            sub_msg = _extract_detail(val)
            if key in ('detail', 'non_field_errors'):
                messages.append(sub_msg)
            else:
                messages.append(f"{sub_msg}")
        return "；".join(messages) if messages else "数据校验错误"
    return str(data)

def custom_exception_handler(exc, context):
    """
    统一全局异常拦截器：
    将 DRF 的 ValidationError 及其他异常规范化输出为 code, message, data 结构，
    确保前端能直接获得具体的中文错误原因，严禁笼统抛出网络错误。
    """
    response = exception_handler(exc, context)

    if response is not None:
        raw_data = response.data
        extracted_message = _extract_detail(raw_data)

        response.data = {
            "code": response.status_code,
            "message": extracted_message,
            "data": raw_data if isinstance(raw_data, dict) else {}
        }
    return response
