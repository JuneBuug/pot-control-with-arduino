from django.contrib import admin
from django.urls import path

from . import views

urlpatterns = [
    path('plant', views.PlantAPI.as_view()),
    path('log', views.LogAPI.as_view()),
    path('led', views.LedAPI.as_view()),
    path('test', views.TestAPI.as_view()),
    path('fcm', views.fcm, name='fcm'),
]
