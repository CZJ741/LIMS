from django.urls import path
from .views import FlowActionLogListView

urlpatterns = [
    path('logs/', FlowActionLogListView.as_view(), name='flow_logs'),
]
