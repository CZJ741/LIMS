from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/auth/', include('apps.authentication.urls')),
    path('api/workflow/', include('apps.workflow.urls')),
    path('api/contract/', include('apps.contract.urls')),
    path('api/entrust/', include('apps.entrust.urls')),
    path('api/sampling/', include('apps.sampling.urls')),
    path('api/laboratory/', include('apps.laboratory.urls')),
    path('api/report/', include('apps.report.urls')),
]
