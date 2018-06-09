from django.contrib import admin
from django.urls import path

from .views import *

urlpatterns = [
    path('plant', PlantAPI.as_view()),
    path('log', LogAPI.as_view()),
    path('led', LedAPI.as_view()),
    path('test', TestAPI.as_view()),
]
